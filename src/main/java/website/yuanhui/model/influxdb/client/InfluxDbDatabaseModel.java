package website.yuanhui.model.influxdb.client;

import website.yuanhui.util.I18NUtil;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class InfluxDbDatabaseModel extends DefaultMutableTreeNode {
    private final IClient db;
    private final List<DefaultMutableTreeNode> databaseNodes = new ArrayList<>();

    public InfluxDbDatabaseModel(IClient db) throws NoSuchAlgorithmException, KeyManagementException {
        super(I18NUtil.getString("InfluxDbDatabaseModel.database"));
        this.db = db;
        init();
    }

    private void init() throws NoSuchAlgorithmException, KeyManagementException {
        List<String> dbNames = db.dbNames();
        databaseNodes.clear();
        for (String dbName : dbNames) {
            DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(new Database(dbName));
            add(dbNode);
            databaseNodes.add(dbNode);
        }
    }

    public List<String> measurementNames(String dbName) throws NoSuchAlgorithmException, KeyManagementException {
        return db.measurementNames(dbName);
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return databaseNodes.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return databaseNodes.size();
    }

}
