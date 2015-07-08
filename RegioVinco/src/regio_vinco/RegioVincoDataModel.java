package regio_vinco;

import audio_manager.AudioManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
    
    //Label regionLabel = new Label();
    
    
    long finishTime; 
    static long timePassed;
    static int scoreTime;
    static int regionsFoundNum, regionsLeftNum, incorrectGuessesNum;
    
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
           changeSubRegionColorOnMap(game, firstLabel.getText().getText(), Color.GREEN);
            
	
        }
         MovableText lastOne = subRegionStack.peekFirst();
            String lastRegion = lastOne.getText().getText();
            if(redSubRegions.contains(lastRegion)){
                changeSubRegionColorOnMap(game, lastRegion, subRegionToColorMappings.get(lastRegion));
            }
        
	startTextStackMovingDown();
    }

    // ACCESSOR METHODS
    public String getRegionName() {
	return regionName;
    }
    
    public int getScore(){
        int score = 1000;
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
        Color pixelColor = mapPixelReader.getColor(x, y);
        String overSubRegion = colorToSubRegionMappings.get(pixelColor);
        if(overSubRegion == null){
            game.regionLabel.setVisible(false);
        }
        if(pixelColor != Color.PINK){
            game.regionLabel.setVisible(true);
            game.regionLabel.setText(overSubRegion);
            game.regionLabel.setFont(Font.font("Serif", 25));
            game.regionLabel.setTextFill(Color.WHITE);
            game.regionLabel.setLayoutX(1000);
            game.regionLabel.setLayoutY(250);
        }
    }
    
    public void navigationMapSelection(RegioVincoGame game, int x, int y) throws InvalidXMLFileFormatException{
        Color pixelColor = mapPixelReader.getColor(x, y);
	String clickedSubRegion = colorToSubRegionMappings.get(pixelColor);
	if ((clickedSubRegion == null)) {
	    return;
	}
        colorToSubRegionMappings.clear();
        ((RegioVincoGame) game).reloadMap(clickedSubRegion);
        
        System.out.println("selected");
    }
    public void respondToMapSelection(RegioVincoGame game, int x, int y) {
        // THIS IS WHERE WE'LL CHECK TO SEE IF THE
	// PLAYER CLICKED NO THE CORRECT SUBREGION
	Color pixelColor = mapPixelReader.getColor(x, y);
	String clickedSubRegion = colorToSubRegionMappings.get(pixelColor);
	if ((clickedSubRegion == null) || (subRegionStack.isEmpty())) {
	    return;
	}
	if (clickedSubRegion.equals(subRegionStack.get(0).getText().getText())) {
	    // YAY, CORRECT ANSWER
            regionsFoundNum++;
            regionsLeftNum--;
	    game.getAudio().play(SUCCESS, false);

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
		game.getAudio().stop(TRACKED_SONG);
		game.getAudio().play(AFGHAN_ANTHEM, false);
	    }
	} else {
	    if (!redSubRegions.contains(clickedSubRegion)) {
		// BOO WRONG ANSWER
		game.getAudio().play(FAILURE, false);
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
	// THIS GAME ONLY PLAYS AFGHANISTAN
        //need to clear the labels on the bottom of the screen
        Pane guiLayer = ((RegioVincoGame)game).getGuiLayer();
        guiLayer.getChildren().remove(4, guiLayer.getChildren().size());
        
	regionName = "Afghanistan";
	subRegionsType = "Provinces";
        
        //((RegioVincoGame)game).reloadMap();
	// LET'S CLEAR THE DATA STRUCTURES
	colorToSubRegionMappings.clear();
	subRegionToColorMappings.clear();
	subRegionStack.clear();
	redSubRegions.clear();
        
        regionsFoundNum = 0;
        incorrectGuessesNum = 0;
        
        
        //make a guiLayer
        //Pane gameLayer = ((RegioVincoGame)game).getGuiLayer();
        
       
        
        
        
        //Add region title
        //Pane guiLayer = ((RegioVincoGame)game).getGuiLayer();
        Label regionTitle = new Label(REGION_TITLE);
        regionTitle.setTextFill(Color.YELLOW);
        regionTitle.setFont(Font.font("Serif", 30));
        regionTitle.setPrefHeight(44);
        regionTitle.setPrefWidth(400);
        regionTitle.setStyle("-fx-background-color: black;");
        guiLayer.getChildren().add(regionTitle);
        
        regionTitle.relocate(START_X, START_Y + 46);
        
        // INIT THE MAPPINGS - NOTE THIS SHOULD 
	// BE DONE IN A FILE, WHICH WE'LL DO IN
	// FUTURE HOMEWORK ASSIGNMENTS
	colorToSubRegionMappings.put(makeColor(200, 200, 200), "Badakhshan");
	colorToSubRegionMappings.put(makeColor(198, 198, 198), "Nuristan");
	colorToSubRegionMappings.put(makeColor(196, 196, 196), "Kunar");
	colorToSubRegionMappings.put(makeColor(194, 194, 194), "Laghman");
	colorToSubRegionMappings.put(makeColor(192, 192, 192), "Kapisa");
	colorToSubRegionMappings.put(makeColor(190, 190, 190), "Panjshir");
	colorToSubRegionMappings.put(makeColor(188, 188, 188), "Takhar");
	colorToSubRegionMappings.put(makeColor(186, 186, 186), "Kunduz");
	colorToSubRegionMappings.put(makeColor(184, 184, 184), "Baghlan");
	colorToSubRegionMappings.put(makeColor(182, 182, 182), "Parwan");
	colorToSubRegionMappings.put(makeColor(180, 180, 180), "Kabul");
	colorToSubRegionMappings.put(makeColor(178, 178, 178), "Nangrahar");
	colorToSubRegionMappings.put(makeColor(176, 176, 176), "Maidan Wardak");
	colorToSubRegionMappings.put(makeColor(174, 174, 174), "Logar");
	colorToSubRegionMappings.put(makeColor(172, 172, 172), "Paktia");
	colorToSubRegionMappings.put(makeColor(170, 170, 170), "Khost");
	colorToSubRegionMappings.put(makeColor(168, 168, 168), "Samangan");
	colorToSubRegionMappings.put(makeColor(166, 166, 166), "Balkh");
	colorToSubRegionMappings.put(makeColor(164, 164, 164), "Jowzjan");
	colorToSubRegionMappings.put(makeColor(162, 162, 162), "Faryab");
	colorToSubRegionMappings.put(makeColor(160, 160, 160), "Sar-e Pol");
	colorToSubRegionMappings.put(makeColor(158, 158, 158), "Bamyan");
	colorToSubRegionMappings.put(makeColor(156, 156, 156), "Ghazni");
	colorToSubRegionMappings.put(makeColor(154, 154, 154), "Paktika");
	colorToSubRegionMappings.put(makeColor(152, 152, 152), "Badghis");
	colorToSubRegionMappings.put(makeColor(150, 150, 150), "Ghor");
	colorToSubRegionMappings.put(makeColor(148, 148, 148), "Daykundi");
	colorToSubRegionMappings.put(makeColor(146, 146, 146), "Oruzgan");
	colorToSubRegionMappings.put(makeColor(144, 144, 144), "Zabul");
	colorToSubRegionMappings.put(makeColor(142, 142, 142), "Herat");
	colorToSubRegionMappings.put(makeColor(140, 140, 140), "Farah");
	colorToSubRegionMappings.put(makeColor(138, 138, 138), "Nimruz");
	colorToSubRegionMappings.put(makeColor(136, 136, 136), "Helmand");
	colorToSubRegionMappings.put(makeColor(134, 134, 134), "Kandahar");

	// REST THE MOVABLE TEXT
	Pane gameLayer = ((RegioVincoGame)game).getGameLayer();
	gameLayer.getChildren().clear();
        
        ((RegioVincoGame) game).getGUIImages().get(MAP_TYPE).setVisible(true);
        
	for (Color c : colorToSubRegionMappings.keySet()) {
	    String subRegion = colorToSubRegionMappings.get(c);
	    subRegionToColorMappings.put(subRegion, c);
	    Text textNode = new Text(subRegion);
            Label textLabel = new Label();
            textLabel.setGraphic(textNode);
	    //gameLayer.getChildren().add(textLabel);
	    MovableText subRegionText = new MovableText(textNode, textLabel);
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

        try {
            // RELOAD THE MAP
            ((RegioVincoGame) game).reloadMap(WORLD_MAP_FILE_PATH);
        } catch (InvalidXMLFileFormatException ex) {
            Logger.getLogger(RegioVincoDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // set  gameLayer visible
        //((RegioVincoGame) game).getGUIImages().get(MAP_TYPE).setVisible(true);
	// LET'S RECORD ALL THE PIXELS
	pixels = new HashMap();
	for (MovableText mT : subRegionStack) {
	    pixels.put(mT.getText().getText(), new ArrayList());
	}
        //220 110 0 orange
	for (int i = 0; i < mapImage.getWidth(); i++) {
	    for (int j = 0; j < mapImage.getHeight(); j++) {
		Color c = mapPixelReader.getColor(i, j);
		if (colorToSubRegionMappings.containsKey(c)) {
		    String subRegion = colorToSubRegionMappings.get(c);
		    ArrayList<int[]> subRegionPixels = pixels.get(subRegion);
		    int[] pixel = new int[2];
		    pixel[0] = i;
		    pixel[1] = j;
                    
		    subRegionPixels.add(pixel);
		}
	    }
	}
        
         //add labels to bottom
        time = new Label("time");
        Label regionsFound = new Label("Regions Found: ");
        regionsFoundLabel = new Label("00");
        Label regionsLeft = new Label("Regions Left: ");
        regionsLeftLabel = new Label("00");
        Label incorrectGuesses = new Label("Incorrect Guesses: ");
        incorrectGuessesLabel = new Label("00");
        
        //gameLayer.getChildren().add(time);
        
        //time label
        startTime = System.currentTimeMillis();
        time.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        time.setTextFill(Color.ORANGE);
        time.setLayoutX(10);
        time.setLayoutY(635);
        guiLayer.getChildren().add(time);
        
        regionsFound.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsFound.setTextFill(Color.ORANGE);
        regionsFound.setLayoutX(85);
        regionsFound.setLayoutY(635);
        guiLayer.getChildren().add(regionsFound);
        
        regionsFoundLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsFoundLabel.setTextFill(Color.ORANGE);
        regionsFoundLabel.setLayoutX(267);
        regionsFoundLabel.setLayoutY(635);
        guiLayer.getChildren().add(regionsFoundLabel);
        
        regionsLeft.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsLeft.setTextFill(Color.ORANGE);
        regionsLeft.setLayoutX(340);
        regionsLeft.setLayoutY(635);
        guiLayer.getChildren().add(regionsLeft);
        
        regionsLeftLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        regionsLeftLabel.setTextFill(Color.ORANGE);
        regionsLeftLabel.setLayoutX(495);
        regionsLeftLabel.setLayoutY(635);
        guiLayer.getChildren().add(regionsLeftLabel);
        
        incorrectGuesses.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        incorrectGuesses.setTextFill(Color.ORANGE);
        incorrectGuesses.setLayoutX(550);
        incorrectGuesses.setLayoutY(635);
        guiLayer.getChildren().add(incorrectGuesses);
        
        incorrectGuessesLabel.setFont(Font.font("Serif", FontWeight.BOLD, 26));
        incorrectGuessesLabel.setTextFill(Color.ORANGE);
        incorrectGuessesLabel.setLayoutX(762);
        incorrectGuessesLabel.setLayoutY(635);
        guiLayer.getChildren().add(incorrectGuessesLabel);
        
	// RESET THE AUDIO
	AudioManager audio = ((RegioVincoGame) game).getAudio();
	audio.stop(AFGHAN_ANTHEM);

	if (!audio.isPlaying(TRACKED_SONG)) {
	    audio.play(TRACKED_SONG, true);
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
            //bottomOfStack.getText().setFill(Color.color(255.0 , 51.0, 0.0));
            bottomOfStack.getText().setFill(Color.color(255/255.0, 51/255.0, 0/255.0));
	    double bottomY = bottomOfStack.getLabel().getLayoutY() + bottomOfStack.getLabel().getTranslateY();//fix stopping animation layout plus translation
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
