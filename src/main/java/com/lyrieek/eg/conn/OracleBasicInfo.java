package com.lyrieek.eg.conn;

public class OracleBasicInfo extends BasicInfo {

	public OracleBasicInfo(DBConn conn) {
		super(conn);
	}

	@Override
	String getTableQuery() {
//		return "SELECT table_name FROM all_tables WHERE owner = '" + schema + "' order by table_name";
		return "SELECT o.last_ddl_time,t.table_name FROM all_tables t left join all_objects o on t.table_name = o.object_name WHERE t.owner = '" + schema + "' order by o.last_ddl_time";
	}

	@Override
	String getCols(String tableName) {
		return "SELECT COLUMN_NAME,DATA_TYPE,DATA_LENGTH,DATA_PRECISION FROM all_tab_columns WHERE table_name = '" + tableName + "' AND owner = '" + schema + "' order by column_id";
	}

	@Override
	String getCons() {
		return """
				SELECT cols.table_name,cols.column_name name
				FROM user_constraints cons, user_cons_columns cols
				WHERE cons.constraint_name = cols.constraint_name
				AND cons.owner = '%s' AND cons.constraint_type = 'P'
				""".formatted(schema);
	}
}
