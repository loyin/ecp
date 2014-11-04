/* Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.snaker.jfinal.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.snaker.engine.SnakerException;
import org.snaker.engine.access.jdbc.JdbcAccess;
import org.snaker.engine.access.jdbc.JdbcHelper;
import org.snaker.engine.entity.Process;
import org.snaker.jfinal.JfinalHelper;

import com.jfinal.plugin.activerecord.Config;

/**
 * jfinal的数据访问实现类
 * 主要重构getConnection方法
 * 从jfinal的threadlocal中获取数据连接，事务统一由jfinal管理
 * @author yuqs
 * @since 2.0
 */
public class JfinalAccess extends JdbcAccess {
	/**
	 * 从jfinal的threadlocal中获取数据库连接
	 */
	protected Connection getConnection() throws SQLException {
        Config config = JfinalHelper.getConfig();
		Connection conn = config.getThreadLocalConnection();
		if(conn == null) {
			conn = config.getConnection();
            conn.setAutoCommit(true);
		}
		return conn;
	}
	/**
	 * 使用原生JDBC操作BLOB字段
	 */
	@Override
	public void saveProcess(Process process) {
		super.saveProcess(process);
		if(process.getBytes() != null) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			try {
				conn = getConnection();
				pstmt = conn.prepareStatement(PROCESS_UPDATE_BLOB);
//				pstmt.setBytes(1,process.getBytes());
				process.setContent(NonContextualLobCreator.INSTANCE.wrap(NonContextualLobCreator.INSTANCE.createBlob(process.getBytes())));
		        pstmt.setBlob(1, process.getContent());
				pstmt.setString(2, process.getId());
				pstmt.execute();
			} catch (Exception e) {
				throw new SnakerException(e.getMessage(), e.getCause());
			} finally {
				try {
					JdbcHelper.close(pstmt);
				} catch (SQLException e) {
					throw new SnakerException(e.getMessage(), e.getCause());
				}
			}
		}
	}

	/**
	 * 使用原生JDBC操作BLOB字段
	 */
	@Override
	public void updateProcess(Process process) {
		super.updateProcess(process);
		if(process.getBytes() != null) {
			Connection conn = null;
			PreparedStatement pstmt = null;
			try {
				conn = getConnection();
				pstmt = conn.prepareStatement(PROCESS_UPDATE_BLOB);
//				pstmt.setBytes(1, process.getBytes());//改为blob传值//postgresql下的问题
				process.setContent(NonContextualLobCreator.INSTANCE.wrap(NonContextualLobCreator.INSTANCE.createBlob(process.getBytes())));
		        pstmt.setBlob(1, process.getContent());
				pstmt.setString(2, process.getId());
				pstmt.execute();
			} catch (Exception e) {
				throw new SnakerException(e.getMessage(), e.getCause());
			} finally {
				try {
					JdbcHelper.close(pstmt);
				} catch (SQLException e) {
					throw new SnakerException(e.getMessage(), e.getCause());
				}
			}
		}
	}
}
