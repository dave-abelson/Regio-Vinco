package regio_vinco;

import audio_manager.AudioManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import pacg.PointAndClickGame;
import pacg.PointAndClickGameDataModel;
import static regio_vinco.RegioVinco.*;
import xml_utilities.InvalidXMLFileFormatException;
// orange 220 110 0 not 220 110 1
/**
 * This class manages the game data for the Regio Vinco game application. Note
 * that this game is built using the Point & Click Game Framework as its base. 
 * This class contains methods for managing game data and states.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class RegioVincoDataModel extends PointAndClickGameDataModel {
    // THIS IS THE MAP IMAGE THAT WE'LL USE
    private WritableImage mapImage;
    private PixelReader mapPixelReader;
    private PixelWriter mapPixelWriter;
    
    private  long startTime;
    Label time;
    Label regionsFoundLabel;
    Label regionsLeftLabel;
    Label incorrectGuessesLabel;
    Label regionsFound;
    Label regionsLeft;
    Label incorrectGuesses;
    
    //Label regionLabel = new Label();
    //ImageView flag = new ImageView();
    
    
    long finishTime; 
    static long timePassed;
    static int scoreTime;
    static int regionsFoundNum, regionsLeftNum, incorrectGuessesNum;
    
    boolean correct;
    
    // AND OTHER GAME DATA
    private String regionName;
    private String subRegionsType;
    private HashMap<Color, String> colorToSubRegionMappings;
    private HashMap<String, Color> subRegionToColorMappings;
    private HashMap<String, ArrayList<int[]>> pixels;
    private LinkedList<String> redSubRegions;
    private LinkedList<MovableText> subRegionStack;
    
    
    /**
     * Default constructor, it initializes all data structures for managing the
     * Sprites, including the map.
     */
    public RegioVincoDataModel() {
	// INITIALIZE OUR DATA STRUCTURES
	colorToSubRegionMappings = new HashMap();
	subRegionToColorMappings = new HashMap();
	subRegionStack = new LinkedList();
	redSubRegions = new LinkedList();
    }
    //map Image
    public void setMapImage(WritableImage initMapImage) {
	mapImage = initMapImage;
	mapPixelReader = mapImage.getPixelReader();
	mapPixelWriter = mapImage.getPixelWriter();
    }

    public void removeAllButOneFromeStack(RegioVincoGame game) {
	while (subRegionStack.size() > 1) {
            
            Pane gameLayer = ((RegioVincoGame)game).getGameLayer();
            MovableText firstLabel = subRegionStack.peekFirst();
	    //MovableText Label = subRegionStack.removeFirst();
	    //String subRegionName = Label.getText().getText();
            gameLayer.getChildren().remove(firstLabel.getLabel());
            subRegionStack.removeFirst();
            
	    // TURN THE TERRITORY GREEN
            //needs to accomodate leader, capital and flag
           changeSubRegionColorOnMap(game, firstLabel.getRegion(), Color.GREEN);
            
	
        }
        //needs to accomodate leader, capital and flag
         MovableText lastOne = subRegionStack.peekFirst();
         String lastRegion = "";
         try{
            lastRegion = lastOne.getRegion();
         }
         catch(Exception ex){
             System.out.println();
         }
            if(redSubRegions.contains(lastRegion)){
                changeSubRegionColorOnMap(game, lastOne.getRegion(), subRegionToColorMappings.get(lastRegion));
            }
        
	startTextStackMovingDown();
    }

    // ACCESSOR METHODS
    public String getRegionName() {
	return regionName;
    }
    
    public int getScore(){
        int score = 10000;
        score = score - (int)timePassed/1000;
        score = score - (incorrectGuessesNum * 100);
        return score;
    }
    
    public String getGameDuration(){
        return getSecondsAsTimeText(timePassed/1000);
    }
    
    public int getSubRegionAmount(){
        return regionsFoundNum + regionsLeftNum;
    }
    
    public int getIncorrectGuess(){
        return incorrectGuessesNum;
    }
    
    public String getSubRegionsType() {
	return subRegionsType;
    }

    public void setRegionName(String initRegionName) {
	regionName = initRegionName;
    }

    public void setSubRegionsType(String initSubRegionsType) {
	subRegionsType = initSubRegionsType;
    }

    public String getSecondsAsTimeText(long numSeconds) {
	long numHours = numSeconds / 3600;
	numSeconds = numSeconds - (numHours * 3600);
	long numMinutes = numSeconds / 60;
	numSeconds = numSeconds - (numMinutes * 60);

	String timeText = "";
	if (numHours > 0) {
	    timeText += numHours + ":";
	}
	timeText += numMinutes + ":";
	if (numSeconds < 10) {
	    timeText += "0" + numSeconds;
	} else {
	    timeText += numSeconds;
	}
	return timeText;
    }

    public int getRegionsFound() {
	return colorToSubRegionMappings.keySet().size() - subRegionStack.size();
    }

    public int getRegionsNotFound() {
	return subRegionStack.size();
    }
    
    public LinkedList<MovableText> getSubRegionStack() {
	return subRegionStack;
    }
    
    public String getSubRegionMappedToColor(Color colorKey) {
	return colorToSubRegionMappings.get(colorKey);
    }
    
    public Color getColorMappedToSubRegion(String subRegion) {
	return subRegionToColorMappings.get(subRegion);
    }
    
    public HashMap getColorToSubRegionMappings(){
        return colorToSubRegionMappings;
    }
    
    public HashMap getPixelMap(){
        return pixels;
    }
    // MUTATOR METHODS

    public void setPixelMap(HashMap pixels){
        this.pixels = pixels;
    }
    public void addColorToSubRegionMappings(Color colorKey, String subRegionName) {
	colorToSubRegionMappings.put(colorKey, subRegionName);
    }

    public void addSubRegionToColorMappings(String subRegionName, Color colorKey) {
	subRegionToColorMappings.put(subRegionName, colorKey);
    }
    
    public void mouseMoved(RegioVincoGame game, int x, int y){
        String flagPath;
        
        Color pixelColor = mapPixelReader.getColor(x, y);
        String overSubRegion = colorToSubRegionMappings.get(pixelColor);
        
        flagPath = game.path + overSubRegion + "/" + overSubRegion + " flag.png";
        game.flag.setImage(game.loadImage(flagPath));
            
        if(overSubRegion == null){
            //System.out.println(overSubRegion + " OverSubRegion");
            game.regionLabel.setVisible(false);
            game.highScore.setVisible(false);
            game.bestTime.setVisible(false);
            game.leastGuesses.setVisible(false);
            game.flag.setVisible(false);
//            game.flagLabel.setVisible(false);
//            game.flagLabel.setStyle("-fx-background-color: transparent");
        }
        else if(pixelColor != Color.PINK){
            game.flag.setVisible(true);

            game.regionLabel.setVisible(true);
            game.regionLabel.setText(overSubRegion);
            
            File score = new File(game.path  +  overSubRegion + "/" + overSubRegion + " Scores.txt");
            System.out.println(game.path);
            if(score.exists()){
                try {
                    game.sc = new Scanner(score);
                    game.highScore.setText("High Score: " + game.sc.next() );
                    game.bestTime.setText("Best Time: " + game.sc.next());
                    game.leastGuesses.setText("Least Incorrect Guesses: " + game.sc.next());
                
                } catch (FileNotFoundException ex) {
                    System.out.println("File Not Found Exception");
                }
            }
            game.highScore.setVisible(true);
            //game.highScore.setText("High Score: ");
            
            
            game.bestTime.setVisible(true);
            //game.bestTime.setText("Best Time: ");
            
            game.leastGuesses.setVisible(true);
            //game.leastGuesses.setText("Least Guesses: ");
            
            
            game.flag.setLayoutX(940);
            game.flag.setLayoutY(205);
        }
    }
    
    public void navigationMapSelection(RegioVincoGame game, int x, int y) throws InvalidXMLFileFormatException{
        Color pixelColor = mapPixelReader.getColor(x, y);
	String clickedSubRegion = colorToSubRegionMappings.get(pixelColor);
	if ((clickedSubRegion == null)) {
	    return;
	}
        colorToSubRegionMappings.clear();
        ((RegioVincoGame) game).reloadMap(clickedSubRegion, true);
        
        //System.out.println("selected");
    }
    
    public void respondToMapSelection(RegioVincoGame game, int x, int y){
        // THIS IS WHERE WE'LL CHECK TO SEE IF THE
	// PLAYER CLICKED NO THE CORRECT SUBREGION
	Color pixelColor = mapPixelReader.getColor(x, y);
	String clickedSubRegion = colorToSubRegionMappings.get(pixelColor);
	if ((clickedSubRegion == null) || (subRegionStack.isEmpty())) {
	    return;
	}
        correct = false;
        //leader mode
        System.out.println(subRegionStack.get(0).getText().getText());
        if(game.gameMode.equals("LEADER")){
            if(game.getLeader(clickedSubRegion).equals(subRegionStack.get(0).getText().getText())){
                correct = true;
            }
        }
        //flag mode
        if(game.gameMode.equals("FLAG")){
//            if(game.getFlagPath(clickedSubRegion).equals(game.getFlagPath(subRegionStack.get(0).getText().getText()))){
//                correct = true;
//            }
            if(clickedSubRegion.equals(subRegionStack.get(0).getRegion())){
                correct = true;
            }
        }
        //capital mode
        if(game.gameMode.equals("CAPITAL")){
            if(game.getCapital(clickedSubRegion).equals(subRegionStack.get(0).getText().getText())){
                correct = true;
            }
        }
        //name mode
        if(game.gameMode.equals("NAME")){
            if (clickedSubRegion.equals(subRegionStack.get(0).getText().getText())) {
                correct = true;
            }
        }
        
        if(correct){
	    // YAY, CORRECT ANSWER
            //redSubRegions.clear();
            regionsFoundNum++;
            regionsLeftNum--;
            if(((RegioVincoGame) game).effectsOn){
                game.getAudio().play(SUCCESS, false);
            }
                
	    // TURN THE TERRITORY GREEN
	    changeSubRegionColorOnMap(game, clickedSubRegion, Color.GREEN);
            
            
            // REMOVE THE BOTTOM ELEMENT FROM THE STACK
            //peak to find first and then remove it
            Pane gameLayer = ((RegioVincoGame)game).getGameLayer();
            MovableText firstLabel = subRegionStack.peekFirst();
            
            gameLayer.getChildren().remove(firstLabel.getLabel());
            subRegionStack.removeFirst();
                
               // game.updateGUI();
            
            
            
            

	    // AND LET'S CHANGE THE RED ONES BACK TO THEIR PROPER COLORS
	    for (String s : redSubRegions) {
                if(subRegionStack.size() > 1){
                    Color subRegionColor = subRegionToColorMappings.get(s);
                    changeSubRegionColorOnMap(game, s, subRegionColor);
                }   
	    }
	    redSubRegions.clear();

	    startTextStackMovingDown();

	    if (subRegionStack.isEmpty()) {
                finishTime = System.currentTimeMillis();
                timePassed = finishTime - startTime;
		this.endGameAsWin();
                game.gameWon = true;
                game.winScreen.setVisible(true);
                //game.writeHighScoreFile();
		game.getAudio().stop(TRACKED_SONG);
		//game.getAudio().play(AFGHAN_ANTHEM, false);
                
                if(clickedSubRegion.equals("Africa") || clickedSubRegion.equals("Antarctica") || clickedSubRegion.equals("Asia") || clickedSubRegion.equals("Europe") || clickedSubRegion.equals("North America") || clickedSubRegion.equals("South America") || clickedSubRegion.equals("Oceania")){
                    try {
                        if(game.musicPlaying){
                            game.getAudio().loadAudio("ANTHEM", MUSIC_FILE_NAME);
                            game.getAudio().play("ANTHEM", true);
                        }
                    } catch (LineUnavailableException ex) {
                        
                    } catch (InvalidMidiDataException ex) {
                    } catch (MidiUnavailableException ex) {
                    } catch (UnsupportedAudioFileException ex) {
                        
                    } catch (IOException ex) {
                        
                    }
                    
                }
                else if(clickedSubRegion.equals("The World")){
                    try {
                        if(game.musicPlaying){
                            game.getAudio().loadAudio("ANTHEM", MUSIC_FILE_NAME);
                            game.getAudio().play("ANTHEM", true);
                        }
                    } catch (LineUnavailableException ex) {
                        
                    } catch (InvalidMidiDataException ex) {
                    } catch (MidiUnavailableException ex) {
                    } catch (UnsupportedAudioFileException ex) {
                        
                    } catch (IOException ex) {
                        
                    }
                }
                else{
                    String musicPath = game.path + game.currentRegion + " National Anthem.mid";
                    try {
                        if(game.musicPlaying){
                            game.getAudio().loadAudio("ANTHEM", musicPath);
                            //System.out.println(musicPath);
                            game.getAudio().play("ANTHEM", false);
                        }
                    } catch (LineUnavailableException ex) {
                    } catch (InvalidMidiDataException ex) {
                    } catch (MidiUnavailableException ex) {
                    } catch (UnsupportedAudioFileException ex) {
                        
                    } catch (IOException ex) {
                        
                    }
                    
                }
                
	    }
	} else {
	    if (!redSubRegions.contains(clickedSubRegion)) {
		// BOO WRONG ANSWER
                if(((RegioVincoGame) game).effectsOn){
                    game.getAudio().play(FAILURE, false);
                }
                incorrectGuessesNum++;
		// TURN THE TERRITORY TEMPORARILY RED
		changeSubRegionColorOnMap(game, clickedSubRegion, Color.RED);
		redSubRegions.add(clickedSubRegion);
	    }
	}
    }

    public void startTextStackMovingDown() {
	// AND START THE REST MOVING DOWN
	for (MovableText mT : subRegionStack) {
	    mT.setVelocityY(SUB_STACK_VELOCITY);
	}
    }

    public void changeSubRegionColorOnMap(RegioVincoGame game, String subRegion, Color color) {
        // THIS IS WHERE WE'LL CHECK TO SEE IF THE
	// PLAYER CLICKED NO THE CORRECT SUBREGION
	ArrayList<int[]> subRegionPixels = pixels.get(subRegion);
	for (int[] pixel : subRegionPixels) {
	    mapPixelWriter.setColor(pixel[0], pixel[1], color);
	}
    }

    public int getNumberOfSubRegions() {
	return colorToSubRegionMappings.keySet().size();
    }

    /**
     * Resets all the game data so that a brand new game may be played.
     *
     * @param game the RegioVinco game in progress
     */
    @Override
    public void reset(PointAndClickGame game) {
        //need to clear the labels on the bottom of the screen
        Pane guiLayer = ((RegioVincoGame)game).getGuiLayer();
        //guiLayer.getChildren().remove(4, guiLayer.getChildren().size());
        
	regionName = ((RegioVincoGame)game).currentRegion;
	subRegionsType = "Provinces";
        
        //((RegioVincoGame)game).reloadMap();
	// LET'S CLEAR THE DATA STRUCTURES
	//colorToSubRegionMappings.clear();
	//subRegionToColorMappings.clear();
	subRegionStack.clear();
	redSubRegions.clear();
        
        regionsFoundNum = 0;
        incorrectGuessesNum = 0;
        
        
        if(((RegioVincoGame)game).gameOn){
            ((RegioVincoGame)game).regionLabel.setVisible(false);
            ((RegioVincoGame)game).highScore.setVisible(false);
            ((RegioVincoGame)game).bestTime.setVisible(false);
            ((RegioVincoGame)game).leastGuesses.setVisible(false);
            ((RegioVincoGame)game).worldLabel.setVisible(false);
            ((RegioVincoGame)game).continentLabel.setVisible(false);
            ((RegioVincoGame)game).countryLabel.setVisible(false);
            ((RegioVincoGame)game).flag.setVisible(false);
        }
        
        //make a guiLayer
        //Pane gameLayer = ((RegioVincoGame)game).getGuiLayer();
        Image tempMapImage = ((RegioVincoGame)game).loadImage(((RegioVincoGame)game).path + ((RegioVincoGame)game).currentRegion + MAPS_FILE_PATH);
        if(tempMapImage == null){
            //System.out.println("Temp map image is null \n " + path + regionMap + MAPS_FILE_PATH);
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
        ImageView mapView = ((RegioVincoGame)game).getGUIImages().get(MAP_TYPE);
	mapView.setImage(mapImage);
        
        setMapImage(mapImage);
       
        
        
        
        //Add region title
        //Pane guiLayer = ((RegioVincoGame)game).getGuiLayer();
        Label regionTitle = new Label(regionName);
        regionTitle.setTextFill(Color.YELLOW);
        regionTitle.setFont(Font.font("Serif", 30));
        regionTitle.setPrefHeight(44);
        regionTitle.setPrefWidth(400);
        regionTitle.setStyle("-fx-background-color: black;");
        guiLayer.getChildren().add(regionTitle);
        
        regionTitle.relocate(START_X, START_Y + 85);

	// RESET THE MOVABLE TEXT
	Pane gameLayer = ((RegioVincoGame)game).getGameLayer();
	gameLayer.getChildren().clear();
        
        //((RegioVincoGame) game).getGUIImages().get(MAP_TYPE).setVisible(true);
        
	for (Color c : colorToSubRegionMappings.keySet()) {
	    String subRegion = colorToSubRegionMappings.get(c);
            subRegionToColorMappings.put(subRegion, c);
            System.out.println(((RegioVincoGame)game).gameMode);
            Text textNode;
            ImageView flagView = new ImageView();
            Label textLabel = new Label();
            if(((RegioVincoGame)game).gameMode.equals("CAPITAL")){
                if(((RegioVincoGame)game).getCapital(subRegion) != null){
                    textNode = new Text(((RegioVincoGame)game).getCapital(subRegion));
                }
                else{
                    continue;
                }
            }
            else if(((RegioVincoGame)game).gameMode.equals("LEADER")){
                if(((RegioVincoGame)game).getLeader(subRegion) != null){
                    textNode = new Text(((RegioVincoGame)game).getLeader(subRegion));
                }
                else{
                    continue;
                }
            }
            else if(((RegioVincoGame)game).gameMode.equals("FLAG")){
                if(((RegioVincoGame)game).getFlag(subRegion) != null){
                    textNode = new Text();
                    //flagView = new ImageView();
                    flagView.setImage(((RegioVincoGame)game).getFlag(subRegion));
                    textLabel.setGraphic(flagView);
                }
                else{
                    continue;
                }
            }
            else{
                textNode = new Text(subRegion);
            }
            if(!(((RegioVincoGame)game).gameMode.equals("FLAG"))){
                
                    textLabel.setGraphic(textNode);
                
            }
	    //gameLayer.getChildren().add(textLabel);
            MovableText subRegionText;
            if(!(((RegioVincoGame)game).gameMode.equals("FLAG"))){
                subRegionText = new MovableText(textNode, textLabel, subRegion);
            }
            else {
                subRegionText = new MovableText(textNode, textLabel, subRegion, ((RegioVincoGame)game).getFlag(subRegion));
            }
            textLabel.setPrefSize(300, 50);
	    subRegionText.getText().setFill(Color.BLUE);
            subRegionText.getText().setFont(Font.font("Serif", 20));
            textLabel.setStyle("-fx-background-color: rgb(" + c.getRed()*255  +", " + c.getGreen()* 255 + ", " + c.getBlue() * 255 + ")" );
	    //textNode.setX(STACK_X);
            gameLayer.getChildren().add(textLabel);
            textLabel.setLayoutX(STACK_X);
            //textLabel.setLayoutY(STACK_INIT_Y);
            //find a way to add boxes to text
	    subRegionStack.add(subRegionText);
            
	}
	Collections.shuffle(subRegionStack);
        regionsLeftNum = subRegionStack.size();
        if(!(((RegioVincoGame)game).gameMode.equals("FLAG"))){
            int y = STACK_INIT_Y - 10;
            int yInc = STACK_INIT_Y_INC;
            //int tY = 0;
            // NOW FIX THEIR Y LOCATIONS
            for (MovableText mT : subRegionStack) {
                int tY = y +yInc;
                //y - yInc;
                mT.getLabel().setLayoutY(tY);
                yInc -= 50;
            }
        }
        else{
            int y = STACK_INIT_Y - 80;
            //int yInc = STACK_INIT_Y_INC - 40;
            int yInc = STACK_INIT_Y_INC;
            for (MovableText mT : subRegionStack) {
                int tY = y + yInc;
                //y - yInc;
                mT.getLabel().setLayoutY(tY);
                //yInc -= 50;
                //yInc -= mT.getImage().getHeight();
                yInc -= 140;
                //System.out.println(mT.getImage().getHeight()+  "   Image Height");
            }
        }
        

//        try {
//            // RELOAD THE MAP
//            String mapPath = ((RegioVincoGame) game).path + "The World" + MAPS_FILE_PATH;
//            ((RegioVincoGame) game).reloadMap(mapPath);
//        } catch (InvalidXMLFileFormatException ex) {
//            Logger.getLogger(RegioVincoDataModel.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        // set  gameLayer visible
        //((RegioVincoGame) game).getGUIImages().get(MAP_TYPE).setVisible(true);
	// LET'S RECORD ALL THE PIXELS
//	pixels = new HashMap();
//	for (MovableText mT : subRegionStack) {
//	    pixels.put(mT.getText().getText(), new ArrayList());
//	}
        //220 110 0 orange
//	for (int i = 0; i < mapImage.getWidth(); i++) {
//	    for (int j = 0; j < mapImage.getHeight(); j++) {
//		Color c = mapPixelReader.getColor(i, j);
//		if (colorToSubRegionMappings.containsKey(c)) {
//		    String subRegion = colorToSubRegionMappings.get(c);
//		    ArrayList<int[]> subRegionPixels = pixels.get(subRegion);
//		    int[] pixel = new int[2];
//		    pixel[0] = i;
//		    pixel[1] = j;
//                    
//		    subRegionPixels.add(pixel);
//		}
//	    }
//	}
        
         //add labels to bottom
        time = new Label("time");
        regionsFound = new Label("Regions Found: ");
        regionsFoundLabel = new Label("00");
        regionsLeft = new Label("Regions Left: ");
        regionsLeftLabel = new Label("00");
        incorrectGuesses = new Label("Incorrect Guesses: ");
        incorrectGuessesLabel = new Label("00");
        
        //gameLayer.getChildren().add(time);
        
        //time label
        startTime = System.currentTimeMillis();
        time.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        time.setTextFill(Color.ORANGE);
        time.setLayoutX(10);
        time.setLayoutY(635);
        //guiLayer.getChildren().add(time);
        ((RegioVincoGame)game).navigation.getChildren().add(time);
        
        regionsFound.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsFound.setTextFill(Color.ORANGE);
        regionsFound.setLayoutX(85);
        regionsFound.setLayoutY(635);
        //guiLayer.getChildren().add(regionsFound);
        ((RegioVincoGame)game).navigation.getChildren().add(regionsFound);
        
        regionsFoundLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsFoundLabel.setTextFill(Color.ORANGE);
        regionsFoundLabel.setLayoutX(267);
        regionsFoundLabel.setLayoutY(635);
        //guiLayer.getChildren().add(regionsFoundLabel);
        ((RegioVincoGame)game).navigation.getChildren().add(regionsFoundLabel);
        
        regionsLeft.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsLeft.setTextFill(Color.ORANGE);
        regionsLeft.setLayoutX(340);
        regionsLeft.setLayoutY(635);
        //guiLayer.getChildren().add(regionsLeft);
        ((RegioVincoGame)game).navigation.getChildren().add(regionsLeft);
        
        regionsLeftLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsLeftLabel.setTextFill(Color.ORANGE);
        regionsLeftLabel.setLayoutX(495);
        regionsLeftLabel.setLayoutY(635);
        //guiLayer.getChildren().add(regionsLeftLabel);
        ((RegioVincoGame)game).navigation.getChildren().add(regionsLeftLabel);
        
        incorrectGuesses.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        incorrectGuesses.setTextFill(Color.ORANGE);
        incorrectGuesses.setLayoutX(550);
        incorrectGuesses.setLayoutY(635);
        //guiLayer.getChildren().add(incorrectGuesses);
        ((RegioVincoGame)game).navigation.getChildren().add(incorrectGuesses);
        
        incorrectGuessesLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        incorrectGuessesLabel.setTextFill(Color.ORANGE);
        incorrectGuessesLabel.setLayoutX(762);
        incorrectGuessesLabel.setLayoutY(635);
        //guiLayer.getChildren().add(incorrectGuessesLabel);
        ((RegioVincoGame)game).navigation.getChildren().add(incorrectGuessesLabel);
        
	// RESET THE AUDIO
	AudioManager audio = ((RegioVincoGame) game).getAudio();
        //change to current theme
	audio.stop("ANTHEM");
        if(((RegioVincoGame) game).musicPlaying){
            if (!audio.isPlaying(TRACKED_SONG)) {
                audio.play(TRACKED_SONG, true);
            }
        }
	// LET'S GO
	beginGame();
    }
   
    // HELPER METHOD FOR MAKING A COLOR OBJECT
    public static Color makeColor(int r, int g, int b) {
	return Color.color(r/255.0, g/255.0, b/255.0);
    }

    // STATE TESTING METHODS
    // UPDATE METHODS
	// updateAll
	// updateDebugText
    
    /**
     * Called each frame, this thread already has a lock on the data. This
     * method updates all the game sprites as needed.
     *
     * @param game the game in progress
     */
    @Override
    public void updateAll(PointAndClickGame game, double percentage) {
        time.setText(this.getSecondsAsTimeText((System.currentTimeMillis() - startTime) / 1000));
        scoreTime = scoreTime++;
        incorrectGuessesLabel.setText("" + incorrectGuessesNum);
        regionsFoundLabel.setText("" + regionsFoundNum);
        regionsLeftLabel.setText("" + regionsLeftNum);
	for (MovableText mT : subRegionStack) {
	    mT.update(percentage);
	}
	if (!subRegionStack.isEmpty()) {
	    MovableText bottomOfStack = subRegionStack.get(0);
            bottomOfStack.getLabel().setStyle("-fx-background-color: rgb(" + 0  +", " + 255 + ", " + 0 + ")" );
            //double height = bottomOfStack.getImage().getHeight();
            //bottomOfStack.getText().setFill(Color.color(255.0 , 51.0, 0.0));
            bottomOfStack.getText().setFill(Color.color(255/255.0, 51/255.0, 0/255.0));
	    double bottomY = bottomOfStack.getLabel().getLayoutY() + bottomOfStack.getLabel().getTranslateY();//fix stopping animation layout plus translation
	    if(((RegioVincoGame)game).gameMode.equals("FLAG")){
                //bottomY = GAME_HEIGHT - height;
                double height = bottomOfStack.getImage().getHeight();
                if (bottomY >= (GAME_HEIGHT - height)) {
                    double diffY = bottomY - (GAME_HEIGHT - height);
                    for (MovableText mT : subRegionStack) {
                        mT.getLabel().setLayoutY(mT.getLabel().getLayoutY() - diffY);
                        mT.setVelocityY(0);
                        //mT.update(percentage);
                    }
                }
            }
            else{
                if (bottomY >= FIRST_REGION_Y_IN_STACK) {
                    double diffY = bottomY - FIRST_REGION_Y_IN_STACK;
                    for (MovableText mT : subRegionStack) {
                        mT.getLabel().setLayoutY(mT.getLabel().getLayoutY() - diffY);
                        mT.setVelocityY(0);
                        //mT.update(percentage);
                    }
                }
            }
	}
    }

    /**
     * Called each frame, this method specifies what debug text to render. Note
     * that this can help with debugging because rather than use a
     * System.out.print statement that is scrolling at a fast frame rate, we can
     * observe variables on screen with the rest of the game as it's being
     * rendered.
     *
     * @return game the active game being played
     */
    public void updateDebugText(PointAndClickGame game) {
	debugText.clear();
    }
}
