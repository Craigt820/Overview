package sample.utils;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;

public abstract class DBUtils<T> {

    public abstract void updateItem( T item, String sql);

}
