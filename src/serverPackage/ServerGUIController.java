package serverPackage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

public class ServerGUIController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Button StartServerBtn;
    @FXML
    private Button StopServerBtn;
    @FXML
    private Button RefreshListBtn;
    @FXML
    private TableView<PlayerForListInServer> PlayerList;
    @FXML
    private TableColumn<PlayerForListInServer, String> UserNameCol;
    @FXML
    private TableColumn<PlayerForListInServer, String> ScoreCol;
    @FXML
    private TableColumn<PlayerForListInServer, String> StatusCol;

    private PlayersListForServer playerListServer;
    private ArrayList<Player> onlinePlayers = new ArrayList<>();
    private static ArrayList<Player> players = new ArrayList<>();
    private Server server = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            updateList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ScoreCol.setComparator(ScoreCol.getComparator().reversed());
        PlayerList.getSortOrder().addAll(ScoreCol);
        StartServerBtn.addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> {
            try {
                server = new Server();
                DatabaseManager.getDBStarted();
                DB_Schema.createSchema();
                PlayerSchema.getPlayersList();
                StartServerBtn.setDisable(true);
                StopServerBtn.setDisable(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        StopServerBtn.setDisable(true);
        StopServerBtn.addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> {
            try {
                PlayerSchema.setAllPlayersOffline();
                StartServerBtn.setDisable(false);
                StopServerBtn.setDisable(true);
                server.stopServer();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        RefreshListBtn.addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> {
            try {
                updateList();
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
            //vbox = playerListServerUpdatable.getVbox();
        });
    }
    private org.json.simple.JSONArray getPlayersJson(ArrayList<Player> playerList) {
        org.json.simple.JSONArray playersJson = new org.json.simple.JSONArray();
        for(Player p: playerList ) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Email", p.getEmail());
            jsonObject.put("Username", p.getUserName());
            jsonObject.put("Status", p.getStatus());
            jsonObject.put("Score", p.getScore());
            playersJson.add(jsonObject);
        }
        return playersJson;
    }
    private void updateList() throws JSONException {
        PlayerList.getItems().clear();
        playerListServer = new PlayersListForServer();
        players = PlayerSchema.getPlayersList();
        onlinePlayers = PlayerSchema.getAllPlayersOnline();
        org.json.simple.JSONArray playersOnlineArray = getPlayersJson(players);
        JSONArray arrayJson = new JSONArray(playersOnlineArray);
        PlayerList.setEditable(false);

        UserNameCol.setCellValueFactory(new PropertyValueFactory<PlayerForListInServer,String>("username"));
        ScoreCol.setCellValueFactory(new PropertyValueFactory<PlayerForListInServer, String>("score"));
        StatusCol.setCellValueFactory(new PropertyValueFactory<PlayerForListInServer, String>("status"));

        for (int i = 0, size = arrayJson.length(); i < size; i++) {
            org.json.JSONObject objectInArray = arrayJson.getJSONObject(i);
            String username =  objectInArray.get("Username").toString();
            int score = Integer.parseInt(objectInArray.get("Score").toString());
            int status = Integer.parseInt(objectInArray.get("Status").toString());
            PlayerList.getItems().add(new PlayerForListInServer(username, score, status));
        }
    }
}
