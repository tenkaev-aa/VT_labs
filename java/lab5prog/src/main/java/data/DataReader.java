package data;


import java.io.IOException;

public interface DataReader {
    String readString(String prompt, String errorMessage) throws IOException;
    Long readLong(String prompt, String errorMessage, boolean mustBePositive) throws IOException;
    Integer readInt(String prompt, String errorMessage, boolean mustBePositive) throws IOException;
    Float readFloat(String prompt, String errorMessage, boolean mustBePositive) throws IOException;
    Double readDouble(String prompt, String errorMessage, boolean mustBePositive) throws IOException;
    <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass, String errorMessage) throws IOException;
}