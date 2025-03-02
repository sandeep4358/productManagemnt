package com.test.script;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.test.dto.Product;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductDataGenerator {




        public void generateData(String[] args) {
            // Predefined arrays for categories and brands
            String[] categories = {
                    "Electronics", "Audio", "Computers", "Footwear",
                    "Home Appliances", "Photography", "Fashion", "Toys",
                    "Books", "Sports"
            };

            String[] brands = {
                    "Apple", "Samsung", "Sony", "Dell", "Nike", "Adidas",
                    "KitchenAid", "Instant Pot", "LG", "Canon", "Microsoft", "HP"
            };

            List<Product> products = new ArrayList<>();
            Random random = new Random();

            // Define start and end dates for createdAt timestamp
            LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
            // Formatter for ISO date time strings
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            // Calculate total seconds between start and end dates
            long secondsBetween = Duration.between(startDate, endDate).getSeconds();

            // Generate 1000 product records
            for (int i = 1; i <= 1000; i++) {
                // Randomly choose a date for createdAt
                long randomSeconds = (long) (random.nextDouble() * secondsBetween);
                LocalDateTime createdDate = startDate.plusSeconds(randomSeconds);
                // Randomly add 0-30 days for updatedAt (ensure updatedAt is after createdAt)
                int extraDays = random.nextInt(31);
                LocalDateTime updatedDate = createdDate.plusDays(extraDays);

                Product product = new Product(
                        i,
                        "Product " + i,
                        "This is a high-quality product number " + i + " that meets real-time market demands.",
                        Math.round((random.nextDouble() * (1000 - 10) + 10) * 100.0) / 100.0,
                        categories[random.nextInt(categories.length)],
                        random.nextInt(501),  // Stock between 0 and 500
                        brands[random.nextInt(brands.length)],
                        createdDate.format(formatter) + "Z",
                        updatedDate.format(formatter) + "Z"
                );
                products.add(product);
            }

            // Write the products list to a JSON file using Jackson
            ObjectMapper mapper = new ObjectMapper();
            // For pretty printing
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            try {
                mapper.writeValue(new File("products.json"), products);
                System.out.println("Generated products.json with 1000 product records.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

