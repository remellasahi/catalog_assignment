import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;
import java.math.*;

public class Main {
    public static void main(String[] args) {
        try {
            String[] inputs = {"inputs/tc1.json", "inputs/tc2.json"};

            for (String inputFile : inputs) {
                System.out.println("Processing file: " + inputFile);
                JsonObject inputData = parseJsonFromFile(inputFile);
                JsonObject keys = inputData.getAsJsonObject("keys");
                int k = keys.get("k").getAsInt();
                List<Point> pts = extractPoints(inputData);
                BigDecimal constant = calculateConstant(pts, k);

                System.out.println("Constant term (secret): " + constant + "\n");
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static JsonObject parseJsonFromFile(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        return JsonParser.parseReader(reader).getAsJsonObject();
    }
    private static List<Point> extractPoints(JsonObject inputData) {
        List<Point> pts = new ArrayList<>();

        for (String key : inputData.keySet()) {
            if (!key.equals("keys")) {
                int x_p = Integer.parseInt(key); 
                JsonObject pointData = inputData.getAsJsonObject(key);
                int base = pointData.get("base").getAsInt();
                String encodedValue = pointData.get("value").getAsString();
                BigInteger y_p = new BigInteger(encodedValue, base); 
                pts.add(new Point(x_p, y_p));
            }
        }

        return pts;
    }
    
    private static BigDecimal calculateConstant(List<Point> pts, int k) {
        BigDecimal constant = BigDecimal.ZERO;

        for (int i = 0; i < k; i++) {
            BigDecimal lagrangeTerm = BigDecimal.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigDecimal xi = BigDecimal.valueOf(pts.get(i).x_p);
                    BigDecimal xj = BigDecimal.valueOf(pts.get(j).x_p);
                    lagrangeTerm = lagrangeTerm.multiply(xj.negate())
                            .divide(xi.subtract(xj), 20, RoundingMode.HALF_UP);
                }
            }

            BigDecimal yi = new BigDecimal(pts.get(i).y_p);
            constant = constant.add(lagrangeTerm.multiply(yi));
        }

        return constant;
    }
}

class Point {
    int x_p;
    BigInteger y_p;

    Point(int x_p, BigInteger y_p) {
        this.x_p = x_p;
        this.y_p = y_p;
    }
}
