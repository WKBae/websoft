package com.dbdbdeep.websoft.models;

import com.dbdbdeep.websoft.database.Database;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;

public class FolderModel {

	public static void createTable() throws SQLException {
		try (Connection conn = Database.getDatabase().getConnection()) {
			PreparedStatement stmt = conn.prepareStatement(
					"CREATE TABLE folder (" +
							"id INT NOT NULL AUTO_INCREMENT," +
							"parent INT," +
							"name VARCHAR(100) NOT NULL," +
							"owner INT NOT NULL," +
							"created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"PRIMARY KEY (id)," +
							"FOREIGN KEY (parent) REFERENCES folder(id)" +
							"ON DELETE CASCADE," +
							"FOREIGN KEY (owner) REFERENCES user(id)" +
							"ON DELETE CASCADE," +
							"UNIQUE (parent, name)" +
							") DEFAULT CHARACTER SET utf8;"
			);
			stmt.execute();
		}
	}

	public static FolderModel get(int id) throws SQLException {
		Database db = Database.getDatabase();
		Object idColumn = db.selectSingleColumn("SELECT 1 FROM folder WHERE id=?", id);
		if (idColumn == null) return null;
		else return new FolderModel(id);
	}

	static FolderModel getUnchecked(int id) {
		return new FolderModel(id);
	}

	public static FolderModel getRoot(UserModel user) throws SQLException {
		Database db = Database.getDatabase();
		Integer rootId = (Integer) db.selectSingleColumn("SELECT id FROM folder WHERE parent IS NULL AND owner=?", user.getId());
		if (rootId != null) {
			return new FolderModel(rootId);
		} else {
			return create(null, "", user);
		}
	}

	public static FolderModel create(FolderModel parent, String name, UserModel owner, Date created) throws SQLException {
		Database db = Database.getDatabase();
		Integer id = db.insertGetId(
				"INSERT INTO user (parent, name, owner, created) VALUES (?, ?, ?, ?)",
				parent != null ? parent.getId() : null,
				name,
				owner != null ? owner.getId() : null,
				new Timestamp(created.getTime())
		);
		return (id == null) ? null : new FolderModel(id);
	}

	public static FolderModel create(FolderModel parent, String name, UserModel owner) throws SQLException {
		Database db = Database.getDatabase();
		Integer id = db.insertGetId(
				"INSERT INTO user (parent, name, owner) VALUES (?, ?, ?)",
				parent != null ? parent.getId() : null,
				name,
				owner != null ? owner.getId() : null
		);
		return (id == null) ? null : new FolderModel(id);
	}

	public void delete() throws SQLException {
		Database.getDatabase().update("DELETE FROM folder WHERE id=?", this.id);
	}

	private final int id;

	private FolderModel(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public FolderModel getParent() throws SQLException {
		Integer parentId = (Integer) Database.getDatabase().selectSingleColumn("SELECT parent FROM folder WHERE id=?", this.id);
		return (parentId == null) ? null : FolderModel.get(parentId);
	}

	public void setParent(FolderModel parent) throws SQLException {
		Integer parentId = parent != null ? parent.getId() : null;
		Database.getDatabase().update("UPDATE folder SET parent=? WHERE id=?", parentId, this.id);
	}

	public String getName() throws SQLException {
		return (String) Database.getDatabase().selectSingleColumn("SELECT name FROM folder WHERE id=?", this.id);
	}

	public void setName(String name) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET name=? WHERE id=?", name, this.id);
	}

	public UserModel getOwner() throws SQLException {
		int ownerId = (Integer) Database.getDatabase().selectSingleColumn("SELECT owner FROM folder WHERE id=?", this.id);
		return UserModel.get(ownerId);
	}

	public void setOwner(UserModel owner) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET owner=? WHERE id=?", owner.getId(), this.id);
	}

	public Date getCreatedDate() throws SQLException {
		Timestamp date = (Timestamp) Database.getDatabase().selectSingleColumn("SELECT created FROM folder WHERE id=?", this.id);
		return new Date(date.getTime());
	}

	public void setCreatedDate(Date created) throws SQLException {
		Database.getDatabase().update("UPDATE folder SET created=? WHERE id=?", new Timestamp(created.getTime()), this.id);
	}

	public FolderModel getFolder(String name) throws SQLException {
		Integer id = (Integer) Database.getDatabase().selectSingleColumn("SELECT id FROM folder WHERE parent=? AND name=?", this.id, name);
		return (id == null) ? null : FolderModel.getUnchecked(id);
	}

	public FileModel getFile(String name) throws SQLException {
		Integer id = (Integer) Database.getDatabase().selectSingleColumn("SELECT id FROM file WHERE parent=? AND name=?", this.id, name);
		return (id == null) ? null : FileModel.getUnchecked(id);
	}

	private FolderModel[] readFolderIds(ResultSet rs) throws SQLException {
		LinkedList<FolderModel> models = new LinkedList<>();
		while (rs.next()) {
			models.add(FolderModel.getUnchecked(rs.getInt(1)));
		}
		return models.toArray(new FolderModel[0]);
	}

	private FileModel[] readFileIds(ResultSet rs) throws SQLException {
		LinkedList<FileModel> models = new LinkedList<>();
		while (rs.next()) {
			models.add(FileModel.getUnchecked(rs.getInt(1)));
		}
		return models.toArray(new FileModel[0]);
	}

	public FolderModel[] getFolders() throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT id FROM folder WHERE parent=?")) {
			stmt.setInt(1, this.id);
			try (ResultSet rs = stmt.executeQuery()) {
				return readFolderIds(rs);
			}
		}
	}

	public FileModel[] getFiles() throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT id FROM file WHERE parent=?")) {
			stmt.setInt(1, this.id);
			try (ResultSet rs = stmt.executeQuery()) {
				return readFileIds(rs);
			}
		}
	}

	private static String escapeWildcards(String value) {
		return value.replace("!", "!!")
				.replace("%", "!%")
				.replace("_", "!_")
				.replace("[", "![");
	}

	public FolderModel[] searchFolders(String name) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT id FROM folder WHERE parent=? AND name LIKE ? ESCAPE '!'")) {
			stmt.setInt(1, this.id);
			String nameEsc = escapeWildcards(name);
			stmt.setString(2, "%" + nameEsc + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				return readFolderIds(rs);
			}
		}
	}

	public FileModel[] searchFiles(String name) throws SQLException {
		Database db = Database.getDatabase();
		try (Connection conn = db.getConnection();
		     PreparedStatement stmt = conn.prepareStatement("SELECT id FROM file WHERE parent=? AND name LIKE ? ESCAPE '!'")) {
			stmt.setInt(1, this.id);
			String nameEsc = escapeWildcards(name);
			stmt.setString(2, "%" + nameEsc + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				return readFileIds(rs);
			}
		}
	}

	public FolderModel transverse(String[] path) throws SQLException {
		return transverse(path, 0);
	}

	private FolderModel transverse(String[] path, int idx) throws SQLException {
		if (idx >= path.length) return this;

		if (path[idx].equals(this.getName())) {
			if (idx + 1 == path.length) {
				return this;
			} else {
				FolderModel child = this.getFolder(path[idx + 1]);
				return (child == null) ? null : child.transverse(path, idx + 1);
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null) return false;
		if (obj instanceof FolderModel) {
			FolderModel f = (FolderModel) obj;
			return f.id == this.id;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.id;
	}

}
