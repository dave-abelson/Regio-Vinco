package regio_vinco;

import audio_manager.AudioManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
//import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import pacg.PointAndClickGame;
import static regio_vinco.RegioVinco.*;
import world_data.*;
import xml_utilities.InvalidXMLFileFormatException;
import xml_utilities.XMLUtilities;


/**
 * This class is a concrete PointAndClickGame, as specified in The PACG
 * Framework. Note that this one plays Regio Vinco.
 *
 * @author McKillaGorilla
 */
public class RegioVincoGame extends PointAndClickGame {

    // THIS PROVIDES GAME AND GUI EVENT RESPONSES
    RegioVincoController controller;

    // THIS PROVIDES MUSIC AND SOUND EFFECTS
    AudioManager audio;
    
    Label nameLabel;
    Label worldLabel;
    Label continentLabel;
    Label countryLabel;
    Label regionLabel;
    
    // THESE ARE THE GUI LAYERS
    Pane backgroundLayer;
    Pane gameLayer;
    Pane guiLayer;
    Pane splash;
    Pane navigation;
    Pane helpScreen;
    Pane settingsScreen;
    
    WorldDataManager wdm;
    XMLUtilities xml;
    
    String currentRegion;
    String continentRegion;
    
    boolean musicPlaying;
    boolean effectsOn;
    /**
     * Get the game setup.
     */
    public RegioVincoGame(Stage initWindow, WorldDataManager world) {
	super(initWindow, APP_TITLE, TARGET_FRAME_RATE);
        wdm = world;
	initAudio();
    }
    
    public AudioManager getAudio() {
	return audio;
    }
    
    public Pane getGameLayer() {
	return gameLayer;
    }
    
    public Pane getGuiLayer() {
        return guiLayer;
    }
    
    public Pane getsplash(){
        return splash;
    }
    
    public Pane getNavigation(){
        return navigation;
    }
    
    public void WorldLabel() throws InvalidXMLFileFormatException{
        this.reloadMap(worldLabel.getText());
    }
    
    public void ContinentLabel() throws InvalidXMLFileFormatException{
        this.reloadMap(continentLabel.getText());
    }
    
    public void CountryLabel() throws InvalidXMLFileFormatException{
        this.reloadMap(countryLabel.getText());
        
    }
    /**
     * Initializes audio for the game.
     */
    private void initAudio() {
	audio = new AudioManager();
	try {
	    audio.loadAudio(TRACKED_SONG, TRACKED_FILE_NAME);
	    audio.play(TRACKED_SONG, true);

	    audio.loadAudio(AFGHAN_ANTHEM, AFGHAN_ANTHEM_FILE_NAME);
	    audio.loadAudio(SUCCESS, SUCCESS_FILE_NAME);
	    audio.loadAudio(FAILURE, FAILURE_FILE_NAME);
	} catch (Exception e) {
	    
	}
    }

    // OVERRIDDEN METHODS - REGIO VINCO IMPLEMENTATIONS
    // initData
    // initGUIControls
    // initGUIHandlers
    // reset
    // updateGUI
    /**
     * Initializes the complete data model for this application, forcing the
     * setting of all game data, including all needed SpriteType objects.
     */
    @Override
    public void initData() {
	// INIT OUR DATA MANAGER
	data = new RegioVincoDataModel();
	data.setGameDimensions(GAME_WIDTH, GAME_HEIGHT);

	boundaryLeft = 0;
	boundaryRight = GAME_WIDTH;
	boundaryTop = 0;
	boundaryBottom = GAME_HEIGHT;
    }

    /**
     * For initializing all GUI controls, specifically all the buttons and
     * decor. Note that this method must construct the canvas with its custom
     * renderer.
     */
    @Override
    public void initGUIControls() {
	// LOAD THE GUI IMAGES, WHICH INCLUDES THE BUTTONS
	// THESE WILL BE ON SCREEN AT ALL TIMES
	backgroundLayer = new Pane();
	addStackPaneLayer(backgroundLayer);
	addGUIImage(backgroundLayer, BACKGROUND_TYPE, loadImage(BACKGROUND_FILE_PATH), BACKGROUND_X, BACKGROUND_Y);
	
	
	// THEN THE GAME LAYER
	gameLayer = new Pane();
	addStackPaneLayer(gameLayer);
	
	// THEN THE GUI LAYER
	guiLayer = new Pane();
	addStackPaneLayer(guiLayer);
	addGUIImage(guiLayer, TITLE_TYPE, loadImage(TITLE_FILE_PATH), TITLE_X, TITLE_Y);
	addGUIButton(guiLayer, START_TYPE, loadImage(START_BUTTON_FILE_PATH), START_X, START_Y);
	addGUIButton(guiLayer, EXIT_TYPE, loadImage(EXIT_BUTTON_FILE_PATH), EXIT_X, EXIT_Y);
	guiButtons.get(START_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(EXIT_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(START_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        guiButtons.get(EXIT_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
	// NOTE THAT THE MAP IS ALSO AN IMAGE, BUT
	// WE'LL LOAD THAT WHEN A GAME STARTS, SINCE
	// WE'LL BE CHANGING THE PIXELS EACH TIME
	// FOR NOW WE'LL JUST LOAD THE ImageView
	// THAT WILL STORE THAT IMAGE
	
	// NOW LOAD THE WIN DISPLAY, WHICH WE'LL ONLY
	// MAKE VISIBLE AND ENABLED AS NEEDED
	ImageView winView = addGUIImage(guiLayer, WIN_DISPLAY_TYPE, loadImage(WIN_DISPLAY_FILE_PATH), WIN_X, WIN_Y);
	winView.setVisible(false);
        
        navigation = new Pane();
        addStackPaneLayer(navigation);
        navigation.setStyle("-fx-background-color: black;");
        
        addGUIImage(navigation, TITLE_TYPE, loadImage(TITLE_FILE_PATH), TITLE_X, TITLE_Y);
        addGUIButton(navigation, SETTINGS_TYPE, loadImage(SETTINGS_BUTTON_FILE_PATH), SETTINGS_BUTTON_X, SETTINGS_BUTTON_Y);
        addGUIButton(navigation, HELP_TYPE, loadImage(HELP_BUTTON_FILE_PATH), HELP_BUTTON_X, HELP_BUTTON_Y);
        guiButtons.get(SETTINGS_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(SETTINGS_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        guiButtons.get(HELP_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(HELP_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        nameLabel = new Label();
        nameLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setLayoutX(SPLASH_TITLE_X - 50);
        nameLabel.setLayoutY(SPLASH_TITLE_Y + 30);
       
        worldLabel = new Label();
        worldLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        worldLabel.setTextFill(Color.YELLOW);
        worldLabel.setText("The World");
        worldLabel.setLayoutX(50);
        worldLabel.setLayoutY(630);
        
        continentLabel = new Label();
        continentLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        continentLabel.setTextFill(Color.YELLOW);
        //continentLabel.setText("The World");
        continentLabel.setLayoutX(200);
        continentLabel.setLayoutY(630);
        
        
        countryLabel = new Label();
        countryLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        countryLabel.setTextFill(Color.YELLOW);
        //continentLabel.setText("The World");
        countryLabel.setLayoutX(300);
        countryLabel.setLayoutY(630);
        
        regionLabel = new Label();
        
        //name mode button
        addGUIButton(navigation, NAME_MODE_TYPE, loadImage(NAME_MODE_BUTTON_FILE_PATH), NAME_MODE_X, NAME_MODE_Y);
        guiButtons.get(NAME_MODE_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(NAME_MODE_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        //capital mode button
        addGUIButton(navigation, CAPITAL_MODE_TYPE, loadImage(CAPITAL_MODE_BUTTON_FILE_PATH), CAPITAL_MODE_X, CAPITAL_MODE_Y);
        guiButtons.get(CAPITAL_MODE_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(CAPITAL_MODE_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        //flag mode button
        addGUIButton(navigation, FLAG_MODE_TYPE, loadImage(FLAG_MODE_BUTTON_FILE_PATH), FLAG_MODE_X, FLAG_MODE_Y);
        guiButtons.get(FLAG_MODE_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(FLAG_MODE_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        //leader mode button
        addGUIButton(navigation, LEADER_MODE_TYPE, loadImage(LEADER_MODE_BUTTON_FILE_PATH), LEADER_MODE_X, LEADER_MODE_Y);
        guiButtons.get(LEADER_MODE_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(LEADER_MODE_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        //stop button
        addGUIButton(navigation, STOP_TYPE, loadImage(STOP_BUTTON_FILE_PATH), STOP_X, STOP_Y);
        guiButtons.get(STOP_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(STOP_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        ImageView mapView = new ImageView();
	mapView.setX(MAP_X);
	mapView.setY(MAP_Y);
	guiImages.put(MAP_TYPE, mapView);
	navigation.getChildren().add(mapView);
        navigation.getChildren().add(worldLabel);
        navigation.getChildren().add(nameLabel);
        navigation.getChildren().add(continentLabel);
        navigation.getChildren().add(countryLabel);
        navigation.getChildren().add(regionLabel);
        countryLabel.setVisible(false);
        continentLabel.setVisible(false);
        
        helpScreen = new Pane();
        addStackPaneLayer(helpScreen);
        helpScreen.setStyle("-fx-background-color: black;");
        addGUIButton(helpScreen, RETURN_TYPE, loadImage(RETURN_BUTTON_FILE_PATH), HELP_BUTTON_X, HELP_BUTTON_Y);
        addGUIButton(helpScreen, SETTINGS_TYPE2, loadImage(SETTINGS_BUTTON_FILE_PATH), SETTINGS_BUTTON_X, SETTINGS_BUTTON_Y);
        guiButtons.get(RETURN_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(RETURN_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        guiButtons.get(RETURN_TYPE).setStyle("-fx-background-color: transparent");
        guiButtons.get(SETTINGS_TYPE2).setPadding(Insets.EMPTY);
        guiButtons.get(SETTINGS_TYPE2).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        Label helpLabel = new Label();
        helpLabel.setText("Regio Vinco");
        helpLabel.setTextFill(Color.WHITE);
        helpLabel.setFont(Font.font("Baskerville", 30));
        helpLabel.setLayoutX(400);
        helpLabel.setLayoutY(50);
        helpScreen.getChildren().add(helpLabel);
        
        Text helpText = new Text();
        helpText.setText("The Regio Vinco! app is a great way to learn about the world's regions and test your knowledge of the world around you." + 
                          "\n \n You can play by clicking on the region you want to play and then starting a new game to guess the names of the regions!" +
                            "\n You can win by carefully guessing the correct regions for the zone you picked." +
                            "\n \n This version of Regio Vinco! was created by:"  + "\n\t\t\t\t\t\t\t\t\t\t\t Dave Abelson");
        helpText.setLayoutX(150);
        helpText.setLayoutY(150);
        helpText.setFill(Color.WHITE);
        helpText.setFont(Font.font("Baskerville", 25));
        helpText.setWrappingWidth(800);
        helpScreen.getChildren().add(helpText);
        
        
        settingsScreen = new Pane();
        addStackPaneLayer(settingsScreen);
        settingsScreen.setStyle("-fx-background-color: black;");
        addGUIButton(settingsScreen, RETURN_TYPE2, loadImage(RETURN_BUTTON_FILE_PATH), SETTINGS_BUTTON_X, SETTINGS_BUTTON_Y);
        addGUIButton(settingsScreen, HELP_TYPE2, loadImage(HELP_BUTTON_FILE_PATH), HELP_BUTTON_X, HELP_BUTTON_Y);
        guiButtons.get(RETURN_TYPE2).setPadding(Insets.EMPTY);
        guiButtons.get(RETURN_TYPE2).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        guiButtons.get(RETURN_TYPE2).setStyle("-fx-background-color: transparent");
        guiButtons.get(HELP_TYPE2).setPadding(Insets.EMPTY);
        guiButtons.get(HELP_TYPE2).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        Label settingsLabel = new Label();
        settingsLabel.setText("Regio Vinco Settings");
        settingsLabel.setTextFill(Color.WHITE);
        settingsLabel.setFont(Font.font("Baskerville", 40));
        settingsLabel.setLayoutX(300);
        settingsLabel.setLayoutY(50);
        settingsScreen.getChildren().add(settingsLabel);
        
        Label musicLabel = new Label();
        musicLabel.setText("Music ON/OFF");
        musicLabel.setTextFill(Color.WHITE);
        musicLabel.setFont(Font.font("Baskerville", 25));
        musicLabel.setLayoutX(262);
        musicLabel.setLayoutY(435);
        settingsScreen.getChildren().add(musicLabel);
        
        addGUIButton(settingsScreen, SOUND_TYPE, loadImage(SOUND_BUTTON_FILE_PATH), SOUND_X, SOUND_Y);
        guiButtons.get(SOUND_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(SOUND_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        addGUIButton(settingsScreen, SOUND_OFF_TYPE, loadImage(SOUND_OFF_BUTTON_FILE_PATH), SOUND_OFF_X, SOUND_OFF_Y);
        guiButtons.get(SOUND_OFF_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(SOUND_OFF_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        Label effectsLabel = new Label();
        effectsLabel.setText("Sound Effects ON/OFF");
        effectsLabel.setTextFill(Color.WHITE);
        effectsLabel.setFont(Font.font("Baskerville", 25));
        effectsLabel.setLayoutX(520);
        effectsLabel.setLayoutY(435);
        settingsScreen.getChildren().add(effectsLabel);
        
        addGUIButton(settingsScreen, SOUND_EFFECTS_ON_TYPE, loadImage(SOUND_EFFECTS_ON_BUTTON_FILE_PATH), SOUND_EFFECTS_ON_X, SOUND_EFFECTS_ON_Y);
        guiButtons.get(SOUND_EFFECTS_ON_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(SOUND_EFFECTS_ON_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        addGUIButton(settingsScreen, SOUND_EFFECTS_OFF_TYPE, loadImage(SOUND_EFFECTS_OFF_BUTTON_FILE_PATH), SOUND_EFFECTS_OFF_X, SOUND_EFFECTS_OFF_Y);
        guiButtons.get(SOUND_EFFECTS_OFF_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(SOUND_EFFECTS_OFF_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
        
        
        
        splash = new Pane();
        addStackPaneLayer(splash);
        addGUIImage(splash, BACKGROUND_TYPE, loadImage(BACKGROUND_FILE_PATH), GAME_WIDTH/8, BACKGROUND_Y);
        addGUIImage(splash, SPLASH_TITLE_TYPE, loadImage(SPLASH_TITLE_FILE_PATH), SPLASH_TITLE_X, SPLASH_TITLE_Y);
        addGUIButton(splash, ENTER_TYPE, loadImage(ENTER_BUTTON_FILE_PATH), ENTER_X, ENTER_Y);
        guiButtons.get(ENTER_TYPE).setPadding(Insets.EMPTY);
        guiButtons.get(ENTER_TYPE).setStyle("-fx-background-insets: 0, 0, 1, 2;");
    }
    
    // HELPER METHOD FOR LOADING IMAGES
    private Image loadImage(String imagePath) {	
        File file = new File(imagePath);
        if(!file.exists()){
            return null;
        }
	Image img = new Image("file:" + imagePath);
	return img;
    }
    
    /**
     * For initializing all the button handlers for the GUI.
     */
    @Override
    public void initGUIHandlers() {
	controller = new RegioVincoController(this);

	Button startButton = guiButtons.get(START_TYPE);
	startButton.setOnAction(e -> {
	    controller.processStartGameRequest();
	});

	Button exitButton = guiButtons.get(EXIT_TYPE);
	exitButton.setOnAction(e -> {
	    controller.processExitGameRequest();
	});
        
        Button enterButton = guiButtons.get(ENTER_TYPE);
        enterButton.setOnAction(e-> {
           controller.processEnterGameRequest();
            try {
                reloadMap("The World");
            } catch (InvalidXMLFileFormatException ex) {
                Logger.getLogger(RegioVincoGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        Button helpButton = guiButtons.get(HELP_TYPE);
        helpButton.setOnAction(e-> {
            controller.processHelpRequest();
        });
        
        Button helpButton2 = guiButtons.get(HELP_TYPE2);
        helpButton2.setOnAction(e-> {
            controller.processHelpRequest();
        });
        
        Button settingButton = guiButtons.get(SETTINGS_TYPE);
        settingButton.setOnAction(e-> {
            controller.processSettingsRequest();
        });
        
        Button settingButton2 = guiButtons.get(SETTINGS_TYPE2);
        settingButton2.setOnAction(e-> {
            controller.processSettingsRequest();
        });
        
        
        Button returnButton = guiButtons.get(RETURN_TYPE);
        returnButton.setOnAction(e-> {
            controller.processReturnRequest();
        });
        
        Button returnButton2 = guiButtons.get(RETURN_TYPE2);
        returnButton2.setOnAction(e-> {
            controller.processReturnRequest();
        });
        
        Button nameModeButton = guiButtons.get(NAME_MODE_TYPE);
        nameModeButton.setOnAction(e-> {
            //controller.processNameModeRequest();
        });
        
        Button capitalModeButton = guiButtons.get(CAPITAL_MODE_TYPE);
        capitalModeButton.setOnAction(e-> {
            //controller.processCapitalModeRequest();
        });
        
        Button flagModeButton = guiButtons.get(FLAG_MODE_TYPE);
        flagModeButton.setOnAction(e-> {
            //controller.processFlagModeRequest();
        });
        
        Button leaderModeButton = guiButtons.get(LEADER_MODE_TYPE);
        leaderModeButton.setOnAction(e-> {
            //controller.processLeaderModeRequest();
        });
        
        Button stopButton = guiButtons.get(STOP_TYPE);
        stopButton.setOnAction(e-> {
            //controller.processStopRequest();
        });
        
        Button soundButton = guiButtons.get(SOUND_TYPE);
        soundButton.setOnAction(e-> {
            //controller.processSoundRequest();
        });
        
        Button soundOffButton = guiButtons.get(SOUND_OFF_TYPE);
        soundOffButton.setOnAction(e-> {
            //controller.processSoundOffRequest();
        });
        
        Button soundEffectsOnButton = guiButtons.get(SOUND_EFFECTS_ON_TYPE);
        soundEffectsOnButton.setOnAction(e-> {
            //controller.processSoundEffectsOnRequest();
        });
        
        Button soundEffectsOffButton = guiButtons.get(SOUND_EFFECTS_OFF_TYPE);
        soundEffectsOffButton.setOnAction(e-> {
            //controller.processSoundEffectsOffRequest();
        });

	// MAKE THE CONTROLLER THE HOOK FOR KEY PRESSES
	keyController.setHook(controller);

	// SETUP MOUSE PRESSES ON THE MAP
	ImageView mapView = guiImages.get(MAP_TYPE);
	mapView.setOnMouseClicked(e -> {
            try {
                controller.processMapClickRequest((int) e.getX(), (int) e.getY());
            } catch (InvalidXMLFileFormatException ex) {
                System.out.println("Error");
            }
	});
        
        mapView.setOnMouseMoved(e -> {
            controller.processMouseMoved((int) e.getX(), (int) e.getY());
        });
	
        worldLabel.setOnMouseClicked(e -> {
            try {
                continentRegion = null;
                controller.processWorldLabel();
                continentLabel.setVisible(false);
                countryLabel.setVisible(false);
            } catch (InvalidXMLFileFormatException ex) {
                System.out.println("Error");
            }
        });
        
        continentLabel.setOnMouseClicked(e -> {
            try {
                continentRegion = null;
                controller.processContinentLabel();
                countryLabel.setVisible(false);
            } catch (InvalidXMLFileFormatException ex) {
                System.out.println("Error");
            }
        });
        
        countryLabel.setOnMouseClicked(e -> {
            try {
                controller.processCountryLabel();
            } catch (InvalidXMLFileFormatException ex) {
                System.out.println("Error");
            }
        });
        
        
	// KILL THE APP IF THE USER CLOSES THE WINDOW
	window.setOnCloseRequest(e->{
	    controller.processExitGameRequest();
	});
    }

    /**
     * Called when a game is restarted from the beginning, it resets all game
     * data and GUI controls so that the game may start anew.
     */
    @Override
    public void reset() {
	// IF THE WIN DIALOG IS VISIBLE, MAKE IT INVISIBLE
        //RegioVincoGame game;
	ImageView winView = guiImages.get(WIN_DISPLAY_TYPE);
	winView.setVisible(false);
        splash.setVisible(false);
        helpScreen.setVisible(false);
        settingsScreen.setVisible(false);
        guiLayer.setVisible(false);
        gameLayer.setVisible(false);
        navigation.setVisible(true);
        //this.gameLayer.setVisible(true);
	// AND RESET ALL GAME DATA
//	data.reset(this);
    }
    
    public void loadHelpScreen(){
        helpScreen.setVisible(true);
        settingsScreen.setVisible(false);
    }
    
    public void loadSettingsScreen(){
        settingsScreen.setVisible(true);
        helpScreen.setVisible(false);
    }
    
    public void returnButton(){
        helpScreen.setVisible(false);
        settingsScreen.setVisible(false);
    }
    /**
     * This mutator method changes the color of the debug text.
     *
     * @param initColor Color to use for rendering debug text.
     */
    public static void setDebugTextColor(Color initColor) {
//        debugTextColor = initColor;
    }
    
     // HELPER METHOD FOR MAKING A COLOR OBJECT
    public static Color makeColor(int r, int g, int b) {
	return Color.color(r/255.0, g/255.0, b/255.0);
    }
    
    public WorldDataManager getWorldDataManager(){
        return wdm;
    }
    
    /**
     * Called each frame, this method updates the rendering state of all
     * relevant GUI controls, like displaying win and loss states and whether
     * certain buttons should be enabled or disabled.
     */
    int backgroundChangeCounter = 0;

    @Override
    public void updateGUI() {
	// IF THE GAME IS OVER, DISPLAY THE APPROPRIATE RESPONSE
	if (data.won()) {
            RegioVincoDataModel model = new RegioVincoDataModel();
            //model.finishTime = model.time.getText();
            //clear layers
            //gameLayer.getChildren().clear();
            //gameLayer.setVisible(false);
            getGUIImages().get(MAP_TYPE).setVisible(false);
            guiLayer.getChildren().remove(3, guiLayer.getChildren().size());
            
            
            
            
            //create 
	    ImageView winImage = guiImages.get(WIN_DISPLAY_TYPE);
            guiLayer.getChildren().add(winImage);
	    winImage.setVisible(true);
            
            //winning stat labels
            
            Label region = new Label("Region: " + REGION_NAME);
            region.setFont(Font.font("Serif", FontWeight.BOLD, 26));
            region.setTextFill(Color.DARKBLUE);
            region.setLayoutX(380);
            region.setLayoutY(300);
            guiLayer.getChildren().add(region);
            
            //calculate score
            int scoreNum = model.getScore();
            //scoreNum is greater than zero
            Label score = new Label("Score: " + model.getScore());
            score.setFont(Font.font("Serif", FontWeight.BOLD, 26));
            score.setTextFill(Color.DARKBLUE);
            score.setLayoutX(380);
            score.setLayoutY(345);
            guiLayer.getChildren().add(score);
            
            //fix duation time
            Label gameDur = new Label("Game Duration: " + model.getGameDuration());
            gameDur.setFont(Font.font("Serif", FontWeight.BOLD, 26));
            gameDur.setTextFill(Color.DARKBLUE);
            gameDur.setLayoutX(380);
            gameDur.setLayoutY(390);
            guiLayer.getChildren().add(gameDur);
            
            
            Label subReg = new Label("Sub Regions: " + model.getSubRegionAmount());
            subReg.setFont(Font.font("Serif", FontWeight.BOLD, 26));
            subReg.setTextFill(Color.DARKBLUE);
            subReg.setLayoutX(380);
            subReg.setLayoutY(435);
            guiLayer.getChildren().add(subReg);
            
            Label incGuess = new Label("Incorrect Guesses: " + model.getIncorrectGuess());
            incGuess.setFont(Font.font("Serif", FontWeight.BOLD, 26));
            incGuess.setTextFill(Color.DARKBLUE);
            incGuess.setLayoutX(380);
            incGuess.setLayoutY(480);
            guiLayer.getChildren().add(incGuess);
	}
    }

    public void reloadMap(String regionMap) throws InvalidXMLFileFormatException {
        
        currentRegion = regionMap;
        String path;
        
        if(regionMap.equals("The World")){
            path = DATA_PATH + "The World/";
        }
        else if(continentRegion == null){
            path = DATA_PATH + "The World/" + regionMap + "/";
        }
        else{
            System.out.println(continentRegion);
            path = DATA_PATH + "The World/" + continentRegion + "/" + regionMap + "/";
        }
        
	Image tempMapImage = loadImage(path + regionMap + MAPS_FILE_PATH);
        if(tempMapImage == null){
            System.out.println("Temp map image is null \n " + path + regionMap + MAPS_FILE_PATH);
            return;
        }
	PixelReader pixelReader = tempMapImage.getPixelReader();
	WritableImage mapImage = new WritableImage(pixelReader, (int) tempMapImage.getWidth(), (int) tempMapImage.getHeight());
	PixelWriter pixelWriter = mapImage.getPixelWriter();
        Color borderOrange = Color.rgb(220, 110, 0);
        for(int i = 0; i < mapImage.getWidth(); i++){
            for(int j = 0; j < mapImage.getHeight(); j++){
                if(pixelReader.getColor(i, j).equals(borderOrange)){
                    	pixelWriter.setColor(i, j, Color.BLACK);
                }
                
            }
            
        }
        ImageView mapView = guiImages.get(MAP_TYPE);
	mapView.setImage(mapImage);
       
        //mapView.setVisible(true);
	int numSubRegions = ((RegioVincoDataModel) data).getRegionsFound() + ((RegioVincoDataModel) data).getRegionsNotFound();
	this.boundaryTop = -(numSubRegions * 50);

	// AND GIVE THE WRITABLE MAP TO THE DATA MODEL
	((RegioVincoDataModel) data).setMapImage(mapImage);
        
        
        
        
        Region firstRegion;
        File file = new File(path + regionMap + XML_FILE_PATH);
        //File file = new File(XML_PATH + "The World data.xml");
        if(file.exists()){
            System.out.println("Exists");
        }
            
        //System.out.println(XML_PATH + regionMap + XML_FILE_PATH);
        //xml.loadXMLDocument(XML_PATH + regionMap + XML_FILE_PATH, XML_PATH + "RegionData.xsd");
        //Document doc = xml.loadXMLDocument(XML_PATH + regionMap + XML_FILE_PATH, XML_PATH + "RegionData.xsd");
        wdm.load(file);
        
        nameLabel.setText(regionMap);
        
        if(regionMap == "Africa" || regionMap == "Antarctica" || regionMap == "Asia" || regionMap.equals("Europe") || regionMap == "North America" || regionMap == "South America"){
            continentRegion = regionMap;
            continentLabel.setText(regionMap);
            continentLabel.setVisible(true);
        }
        else if(regionMap == "The World"){
            
        }
        else{
            countryLabel.setText(regionMap);
            countryLabel.setVisible(true);
        }
        
        ((RegioVincoDataModel) data).setPixelMap(new HashMap());
        //Node firstRegionNode = doc.getElementsByTagName("region").item(0);
        //ArrayList<Node> regionList = xml.getChildNodesWithName(firstRegionNode, "sub_region");
        Iterator<String> iterator = wdm.getAllRegions().keySet().iterator();
        
        while(iterator.hasNext()){
            String subRegionName = (String)iterator.next();
            Region region = wdm.getRegion(subRegionName);
            firstRegion = region;
            ((RegioVincoDataModel) data).getColorToSubRegionMappings().put(makeColor(region.getRed(), region.getGreen(), region.getBlue()), subRegionName);
            
            ((RegioVincoDataModel) data).getPixelMap().put(subRegionName, new ArrayList());
        }
//        for(Node node : regionList){
//            Region region = new Region();
//        }
//        
        for(int i = 0; i < mapImage.getWidth(); i++){
            for(int j = 0; j < mapImage.getHeight(); j++){
                Color c = pixelReader.getColor(i, j);
                if(((RegioVincoDataModel) data).getColorToSubRegionMappings().containsKey(c)){
                    String subRegion = (String)((RegioVincoDataModel) data).getColorToSubRegionMappings().get(c);
                    ArrayList<int[]> subRegionPixels = (ArrayList<int[]>)((RegioVincoDataModel) data).getPixelMap().get(subRegion);
                    int[] pixel = new int[2];
                    pixel[0] = i;
                    pixel[1] = j;
                    subRegionPixels.add(pixel);
                    
                    File file2 = new File(path + subRegion + "/"+ subRegion + XML_FILE_PATH);
                    
                    if(!(file2.exists())){
                        pixelWriter.setColor(i, j, Color.PINK);
                    
                    }
                }
            }
        }
        
//        Iterator<String> iteratorC = wdm.getAllRegions().keySet().iterator();
//        
//        while(iteratorC.hasNext()){
//            String subRegionName = iteratorC.next();
//            //Region region = wdm.getRegion(subRegionName);
//            File file2 = new File(path + subRegionName + "/"+ subRegionName + XML_FILE_PATH);
//            PixelReader reader = mapImage.getPixelReader();
//            if(!(file2.exists())){
//                for(int i = 0; i < mapImage.getWidth(); i++){
//                    for(int j = 0; j < mapImage.getHeight(); j++){
//                        Color c = reader.getColor(i,j);
//                        String c2 = (String)((RegioVincoDataModel) data).getColorToSubRegionMappings().get(c);
//                        if(c2 != null){
//                            if(c2.equals(subRegionName)){
//                                pixelWriter.setColor(i, j, Color.PINK);
//                            }
//                        }
//                
//                     }
//            
//                }
//            }
//        }
    }
}
