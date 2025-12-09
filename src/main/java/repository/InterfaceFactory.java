package repository;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface InterfaceFactory<E> {
    E fromTokens(String[] tokens);
    String toLine(E entity);
    E fromResultSet(ResultSet rs) throws SQLException;
}
