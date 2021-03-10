package serverPackage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public  class ClientHandler {

    private AtomicBoolean shutDown = new AtomicBoolean(false);
    private DataInputStream inputStreamFromClient;
    private PrintStream printStreamToClient;
    private Socket clientSocket;
    private Thread clientThread;
    static final HashMap<String, ClientHandler> playerSocketIdentifier =  new HashMap<>();
    static final HashMap<String, Player> playerOnlineIdentifier =  new HashMap<>();
    static ArrayList<Player> players = new ArrayList<>();
    static ArrayList<Player> onlinePlayers = new ArrayList<>();
    static ArrayList<Game> showGames = new ArrayList<>();
    private Player player;
    private String emailLeft;

    public ClientHandler(Socket socket) {
        try {
            emailLeft = "";
            clientSocket = socket;
            inputStreamFromClient = new DataInputStream(clientSocket.getInputStream());
            printStreamToClient = new PrintStream(clientSocket.getOutputStream());
            player = new Player();
            players = PlayerSchema.getPlayersList();
            clientThread = new Thread(() -> {
                try {
                    readFromClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        clientThread.start();
    }
    public void readFromClient(){
        while (!shutDown.get()) {
            String message;
            try {
                message = inputStreamFromClient.readLine();
                handleMessage(message);
            } catch (Exception e) {
                try {
                    connectionClosed();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
    private void handleMessage(String msg){
        JSONParser parser = new JSONParser();
        JSONObject json = new JSONObject();
        if(!msg.equals(null)) {
            try {
                json = (JSONObject) parser.parse(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String type = json.get("REQUEST").toString();
            try {
                switch(type) {
                    case "signIn":
                        handleSignIn(msg);
                        break;
                    case "signUp" :
                        handleSignUp(msg);
                        break;
                    case "playOnline":
                        handlePlayOnline();
                        break;
                    case "sendInvitation":
                        handleSendInvitation(msg);
                        break;
                    case "refuseInvitation":
                        handleRefuseInvitation(msg);
                        break;
                    case "acceptInvitation":
                        handleAcceptInvitation(msg);
                        break;
                    case "playOffline":
                        handlePlayOffline(msg);
                        break;
                    case"GameCord":
                        handleGameCord(msg);
                        break;
                    case "showGames" :
                        handleShowGames(msg);
                        break;
                    case "replayMoves":
                        handleReplayMoves(msg);
                        break;
                    case "statusOffline":
                        handleChangeStatus(msg);
                        break;
                    case "refreshList":
                        handleRefreshList();
                        break;
                    case "leftPlayer":
                        handlePlayerLeft();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    private void handlePlayerLeft() {
        JSONObject json = new JSONObject();
        json.put("RESPONSE", "leftPlayer");
        this.printStreamToClient.println(json.toString());
    }

    private void handleRefreshList() {
        try {
            handlePlayOnline();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSignIn(String msg){
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String email = json.get("Email").toString();
        emailLeft = email;
        String password = json.get("Password").toString();
        boolean result = PlayerSchema.validatePlayer(email, password);
        playerSocketIdentifier.put(email, this);
        JSONObject signInResponse = new JSONObject();
        signInResponse.put("RESPONSE", "signInResponse");
        signInResponse.put("Email", email);
        signInResponse.put("result", result);
        printStreamToClient.println(signInResponse.toString());
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getEmail() == email) {
                playerOnlineIdentifier.put(email, players.get(i));
            }
        }
    }
    private boolean checkIfPlayerEmailExists(String email){
        for (int i = 0; i < players.size(); i++) {
            if (email.equals(players.get(i).getEmail())) {
                return true;
            }
        }
        return false;
    }
    public boolean checkIfPlayerEmailValid(String email){
        if(email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"))
        {
            return true;
        }
        return false;
    }
    public boolean checkIfPlayerNameExists(String name){
        for (int i = 0; i < players.size(); i++) {
            if (name.equals(players.get(i).getUserName())) {
                return true;
            }
        }
        return false;
    }
    public boolean checkIfPlayerNameValid(String name){
        if(name.length() > 0)
        {
            return true;
        }
        return false;
    }
    public boolean checkIfPlayerPasswordValid(String pass){
        if(pass.length() >= 5)
        {
            return true;
        }
        return false;
    }
    private void handleSignUp(String msg) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String name = json.get("Name").toString();
        String email = json.get("Email").toString();
        String password = json.get("Password").toString();
        JSONObject handleErrors = new JSONObject();
        handleErrors.put("RESPONSE", "handleErrorsRes");
        boolean valid=true;

        if(checkIfPlayerEmailExists(email))
        {
            valid=false;
            handleErrors.put("EmailExists", "This mail already exist . please enter another one");
        }
        if(!checkIfPlayerEmailValid(email))
        {
            valid=false;
            handleErrors.put("EmailValid", "please Enter valid mail");
        }
        if(checkIfPlayerNameExists(name))
        {
            valid=false;
            handleErrors.put("NameExists", "This name already exist . please enter another one");
        }
        if(!checkIfPlayerNameValid(name))
        {
            valid=false;
            handleErrors.put("NameValid", "please Enter valid name");
        }
        if(!checkIfPlayerPasswordValid(password))
        {
            valid=false;
            handleErrors.put("PassValid", "password must be at least 5 characters");
        }
        if(valid)
        {
            player = new Player(name, email, password);
            PlayerSchema.createPlayer(player);
            handleErrors.put("SignUpSuccess", "successful sign-up");
        }
        this.printStreamToClient.println(handleErrors.toString());
    }
    private JSONArray getPlayersJson(ArrayList<Player> playerList) {
        JSONArray playersJson = new JSONArray();
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
    private void handlePlayOffline(String msg) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(msg);
        String email = json.get("Email").toString();
        PlayerSchema.updateStatus(email, 2);
        JSONObject offlinePlayResponse = new JSONObject();
        offlinePlayResponse.put("RESPONSE", "playOffline");
        this.printStreamToClient.println(offlinePlayResponse.toString());
    }
    private void handlePlayOnline() {
        try {
            onlinePlayers = PlayerSchema.getAllPlayersOnline();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray playersOnlineArray = getPlayersJson(onlinePlayers);
        JSONObject onlinePlayResponse = new JSONObject();
        onlinePlayResponse.put("RESPONSE", "playOnlineToClient");
        onlinePlayResponse.put("PlayersJson", playersOnlineArray);
        this.printStreamToClient.println(onlinePlayResponse.toString());
    }
    private JSONArray getGamesJson(ArrayList<Game> gameList) {
        JSONArray gamesJson = new JSONArray();
        for(Game g: gameList ) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", g.getId());
            jsonObject.put("firstPlayer", g.getFirst_player());
            jsonObject.put("secondPlayer", g.getSecond_player());
            jsonObject.put("winner", g.getWinner());
            jsonObject.put("board", g.getBoard());
            gamesJson.add(jsonObject);
        }
        return gamesJson;
    }
    private void handleShowGames(String msg){
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String email = json.get("Email").toString();
        try {
            showGames = GameSchema.getSavedGames(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray showGamesArray = getGamesJson(showGames);
        JSONObject showGameJson = new JSONObject();
        showGameJson.put("RESPONSE", "showGames");
        showGameJson.put("GamesJson", showGamesArray);
        this.printStreamToClient.println(showGameJson.toString());
    }
    private void handleReplayMoves(String msg){
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject replayMoves = new JSONObject();
        replayMoves.put("RESPONSE", "replayMoves");
        replayMoves.put("GamesJson", json);
        this.printStreamToClient.println(replayMoves.toString());
    }
    private void handleRefuseInvitation(String msg) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String playerEmail = json.get("playerEmail").toString();
        JSONObject refuse = new JSONObject();
        refuse.put("RESPONSE", "refusedInvitation");
        refuse.put("email", playerEmail);
        playerSocketIdentifier.get(playerEmail).printStreamToClient.println(refuse.toString());
    }
    private void handleAcceptInvitation(String msg) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String playerEmail = json.get("firstPlayerEmail").toString();
        String clientEmail = json.get("secondPlayerEmail").toString();
        int gameID = GameSchema.createGame(playerEmail, clientEmail);
        PlayerSchema.updateStatus(playerEmail, 2);
        PlayerSchema.updateStatus(clientEmail, 2);
        JSONObject acceptJson = new JSONObject();
        acceptJson.put("RESPONSE", "acceptInvitation");
        acceptJson.put("OpponentEmail", playerEmail);
        acceptJson.put("PlayerSender", false);
        acceptJson.put("GameID", gameID);

        JSONObject accept2Json = new JSONObject();
        accept2Json.put("RESPONSE", "acceptInvitation");
        accept2Json.put("OpponentEmail", clientEmail);
        accept2Json.put("PlayerSender", true);
        accept2Json.put("GameID", gameID);

        this.printStreamToClient.println(acceptJson.toString());
        playerSocketIdentifier.get(playerEmail).printStreamToClient.println(accept2Json.toString());
    }

    private void handleGameCord(String msg)   {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int cord = Integer.parseInt(json.get("Coordinates").toString());
        String opponentEmail = json.get("OpponentEmail").toString();
        String place = json.get("Character").toString();
        JSONObject myTurnResponse = new JSONObject();
        myTurnResponse.put("RESPONSE", "myTurn");
        myTurnResponse.put("cord", cord);
        myTurnResponse.put("Character", place);

        playerSocketIdentifier.get(opponentEmail).printStreamToClient.println(myTurnResponse.toString());
    }
    private void handleChangeStatus(String msg) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String email = json.get("playerEmail").toString();
        PlayerSchema.offlinePlayerStatus(email);
    }
    private void handleSendInvitation(String msg) {
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String myEmail = json.get("myEmail").toString();
        String emailOtherPlayer = json.get("emailOtherPlayer").toString();
        JSONObject sendInvitationResponse = new JSONObject();
        sendInvitationResponse.put("RESPONSE", "sendInvitationResponse");
        sendInvitationResponse.put("PlayerInvitationEmail", myEmail.toString());
        playerSocketIdentifier.get(emailOtherPlayer).printStreamToClient.println(sendInvitationResponse.toString());
    }
    private void broadcast (String msg) {
        for (Map.Entry<String,ClientHandler> entry : playerSocketIdentifier.entrySet()){
            entry.getValue().printStreamToClient.println(msg);
        }
    }
    public void connectionClosed () throws IOException {
        JSONObject json = new JSONObject();
        json.put("RESPONSE", "clientLeft");
        json.put("Email", emailLeft);
        broadcast(json.toJSONString());
        playerSocketIdentifier.remove(emailLeft);
        clientThread.stop();
        inputStreamFromClient.close();
        printStreamToClient.close();
        clientSocket.close();
        shutDown.set(true);
    }
}
