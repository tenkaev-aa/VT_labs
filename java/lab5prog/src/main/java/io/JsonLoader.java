package io;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.CityData;
import data.DataLoader;
import exceptions.FileReadException;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class JsonLoader implements DataLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<CityData> loadData(String fileName) throws FileReadException {
        try {
            CityData[] cityDataArray = objectMapper.readValue(new File(fileName), CityData[].class);
            return Arrays.asList(cityDataArray);
        } catch (Exception e) {
            throw new FileReadException("Ошибка при чтении JSON-файла: " + fileName, e);
        }
    }
}