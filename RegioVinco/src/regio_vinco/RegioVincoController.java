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
    
    public void processReturnRequest(){
        game.returnButton();
    }
    
    public void processMapClickRequest(int x, int y) throws InvalidXMLFileFormatException {
	((RegioVincoDataModel)game.getDataModel()).navigationMapSelection(game, x, y);
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
