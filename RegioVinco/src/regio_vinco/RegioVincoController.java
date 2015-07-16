package regio_vinco;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import pacg.KeyPressHook;
import xml_utilities.InvalidXMLFileFormatException;

/**
 * This controller provides the apprpriate responses for all interactions.
 */
public class RegioVincoController implements KeyPressHook {
    RegioVincoGame game;
    
    public RegioVincoController(RegioVincoGame initGame) {
	game = initGame;
    }
    
    public void processStartGameRequest() {
	game.reset();
    }
    
    public void processExitGameRequest() {
	game.killApplication();
    }
    
    
    public void processEnterGameRequest(){
        game.reset();
    }
    
    public void processHelpRequest(){
        game.loadHelpScreen();
    }
    
    public void processSettingsRequest(){
        game.loadSettingsScreen();
    }
    
    public void processSoundRequest(){
        game.soundOnRequest();
    }
    
    public void processSoundOffRequest(){
        game.soundOffRequest();
    }
    
    public void processSoundEffectsOnRequest(){
        game.soundEffectsOn();
    }
    
    public void processSoundEffectsOffRequest(){
        game.soundEffectsOff();
    }
    
    public void processReturnRequest(){
        game.returnButton();
    }
    
    public void processMapClickRequest(int x, int y) throws InvalidXMLFileFormatException {
	
        if(game.gameOn){
            ((RegioVincoDataModel)game.getDataModel()).respondToMapSelection(game, x, y);
        }
        else{
            ((RegioVincoDataModel)game.getDataModel()).navigationMapSelection(game, x, y);
        }
    }
    
    public void processWorldLabel() throws InvalidXMLFileFormatException{
        game.WorldLabel();
    }
    
    public void processContinentLabel() throws InvalidXMLFileFormatException{
        game.ContinentLabel();
    }
    
    public void processCountryLabel() throws InvalidXMLFileFormatException{
        game.CountryLabel();
    }
    
    public void processMouseMoved(int x, int y){
        ((RegioVincoDataModel)game.getDataModel()).mouseMoved(game, x, y);
    }
    
    public void processNameModeRequest(){
        //System.out.println("anything");
        game.gameOn = true;
        game.gameMode = "NAME";
        game.gameLayer.setVisible(true);
        game.guiLayer.setVisible(true);
        game.highScoreBottom.setVisible(false);
        game.bestTimeBottom.setVisible(false);
        game.leastGuessesBottom.setVisible(false);
       ((RegioVincoDataModel)game.getDataModel()).reset(game);
    }
    
    public void processCapitalModeRequest(){
        System.out.println("CAPITAL");
        game.gameMode = "CAPITAL";
        game.gameOn = true;
        game.gameLayer.setVisible(true);
        game.guiLayer.setVisible(true);
        game.highScoreBottom.setVisible(false);
        game.bestTimeBottom.setVisible(false);
        game.leastGuessesBottom.setVisible(false);
       ((RegioVincoDataModel)game.getDataModel()).reset(game);
    }
    
    public void processLeaderModeRequest(){
        game.gameMode = "LEADER";   
        game.gameOn = true;
        game.gameLayer.setVisible(true);
        game.guiLayer.setVisible(true);
        game.highScoreBottom.setVisible(false);
        game.bestTimeBottom.setVisible(false);
        game.leastGuessesBottom.setVisible(false);
       ((RegioVincoDataModel)game.getDataModel()).reset(game);
    }
    
    public void processFlagModeRequest(){
        game.gameMode = "FLAG";
        game.gameOn = true;
        game.gameLayer.setVisible(true);
        game.guiLayer.setVisible(true);
        game.highScoreBottom.setVisible(false);
        game.bestTimeBottom.setVisible(false);
        game.leastGuessesBottom.setVisible(false);
       ((RegioVincoDataModel)game.getDataModel()).reset(game);
    }
    
    public void processStopRequest() throws InvalidXMLFileFormatException{
        game.stopGame();
    }
    
    @Override
    
    public void processKeyPressHook(KeyEvent ke)
    {
        KeyCode keyCode = ke.getCode();
        if (keyCode == KeyCode.C)
        {
            try
            {    
                game.beginUsingData();
                RegioVincoDataModel dataModel = (RegioVincoDataModel)(game.getDataModel());
                dataModel.removeAllButOneFromeStack(game);         
            }
            finally
            {
                game.endUsingData();
            }
        }
    }   
}
