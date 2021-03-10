package serverPackage;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class PlayersListForServer {

    private TableView<PlayerForListInServer> tableView;
    private VBox vbox;
    private Label label;
    private ArrayList<Player> onlinePlayers = new ArrayList<>();
    private static ArrayList<Player> players = new ArrayList<>();

    public PlayersListForServer() {
        try {
            players = PlayerSchema.getPlayersList();
            onlinePlayers = PlayerSchema.getAllPlayersOnline();
            org.json.simple.JSONArray playersOnlineArray = getPlayersJson(players);
            JSONArray arrayJson = new JSONArray(playersOnlineArray);
            tableView = new TableView<>();
            vbox = new VBox();
            tableView.setEditable(false);
            TableColumn User_name = new TableColumn("Username");
            TableColumn playerScore = new TableColumn("Score");
            TableColumn playerStatus = new TableColumn("Status");
            label = new Label("Players List");
            tableView.getColumns().add(User_name);
            tableView.getColumns().add(playerScore);
            tableView.getColumns().add(playerStatus);

            User_name.setCellValueFactory(new PropertyValueFactory<PlayerForListInServer,String>("username"));
            playerScore.setCellValueFactory(new PropertyValueFactory<PlayerForListInServer, String>("score"));
            playerStatus.setCellValueFactory(new PropertyValueFactory<PlayerForListInServer, String>("status"));

            for (int i = 0, size = arrayJson.length(); i < size; i++) {
                org.json.JSONObject objectInArray = arrayJson.getJSONObject(i);
                String username =  objectInArray.get("Username").toString();
                int score = Integer.parseInt(objectInArray.get("Score").toString());
                int status = Integer.parseInt(objectInArray.get("Status").toString());
                tableView.getItems().add(new PlayerForListInServer(username, score, status));
            }
            playerScore.setComparator(playerScore.getComparator().reversed());
            tableView.getSortOrder().addAll(playerScore);

        } catch (Exception e) {
            e.printStackTrace();
        }
        vbox.getChildren().addAll(label, tableView);
    }
    public  VBox getVbox(){
        return vbox;
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
}
