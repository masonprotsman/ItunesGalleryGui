
package cs1302.gallery;

/* For GUI Interface */
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Separator;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCode;

/* for image display */
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.Random;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/* For URL Encoding */
import java.net.URLEncoder;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.InputStreamReader;

/* For JSON Parsing */

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/** 
 * Replicates a Gallery Application that uses an Itunes API to obtain images
 * based off of keyword searches and creates a GUI interface with the 
 * results. 
 *
 * @author Mason Protsman and Harli Bott
 * @version 1.0
 * @since 11/12/2017
 */
public class GalleryApp extends Application {

    VBox outerPane = new VBox();
    HBox pane = new HBox();
    HBox bottomPane = new HBox();
    ToolBar toolbar = new ToolBar();
    TextField searchBar = new TextField();
    URL url = null;
    InputStreamReader reader = null;
    GridPane grid = new GridPane();
    Timeline timeline = new Timeline();
    KeyFrame keyframe = null;
    Button playPause = new Button("Play");

    Boolean isPlaying = false;
    Boolean firstStart = true;
    String searchText = "";
    String imagesURL [];
    ImageView images [];
    int size = 0;

    /**
    * Creates a menu bar with two menu's: File and Help
    */
    public void menu(){
	MenuItem exitItem = new MenuItem("E_xit");
	exitItem.setOnAction(event -> {
		Platform.exit();
		System.exit(0);
	    }); // creates a menu item to exit the program when called on

	MenuItem aboutItem = new MenuItem("About");
	aboutItem.setOnAction(event -> {
		about();

	    });

	Menu fileMenu = new Menu("_File"); // creates a file menu
	fileMenu.getItems().add(exitItem); // adds exitItem to the help meu

	Menu helpMenu = new Menu("_Help"); // creats a help menu
	helpMenu.getItems().add(aboutItem); // adds aboutItem to the help menu

	MenuBar menuBar = new MenuBar(); // adds and displays the menu bar
	menuBar.getMenus().addAll(fileMenu,helpMenu);
	outerPane.getChildren().add(menuBar);

    }// menu

    /** 
     * Creates an event handler and new scene for the about menu item
     */
    public void about(){

	Stage stage = new Stage();
	ImageView me = new ImageView("https://scontent-atl3-1.xx.fbcdn.net/v/t1.0-9/12509462_93856274193383_3868851843012217376_n.jpg?oh=940503fce19fd7ee6dc034179b0878ed&oe=5AAD2E92");

	me.setFitWidth(100);
	me.setFitHeight(100);
	me.setPreserveRatio(true);

	Text name = new Text("Name: Mason Protsman");
	Text email = new Text("Student Email: mcp15682@uga.edu");
	Text vers = new Text("Version 1.0");

	//adds everything to a new vbox of a popup menu
	VBox pane = new VBox();
	pane.getChildren().addAll(name, me, email, vers);

	Scene scene = new Scene(pane, 300, 200);
	stage.setTitle("About Me");
	stage.setScene(scene);
	stage.sizeToScene();
	stage.show();

    }//about

    /**
     * Creates and handles a play/pause button.
     */
    public void play(){
      
	toolbar.getItems().add(playPause);

	//handles the action events for the play pause button	
	playPause.setOnAction(event -> {
		if(isPlaying){
		    playPause.setText("Pause");
		    isPlaying = false;
		    randomImages();
		  
		}else{
		    playPause.setText("Play");
		    isPlaying = true;
		}
	    });	
    }// playpause

    /**
     * Creates and handles a search bar.
     */
    public void search(){

	Button update = new Button("Update Images");

	play();

	//handles the action for search bars
	update.setOnAction(event -> {
		searchText = searchBar.getText();// gets the text from the search bar
		query(searchText);
	    });

	toolbar.getItems().addAll(new Separator(), new Text("Search Query: "), searchBar, update);// adds all to the toolbar
	outerPane.getChildren().add(toolbar);//adds all to the main pane

    }//search 
    
    /**
     *  A method that handles and parses the URL from the iTunes API
     */
    public void query(String q){

	String encoded = "";

	try{
	    encoded = URLEncoder.encode(q, "UTF-8");//sends string to the encoder
	} catch(java.io.UnsupportedEncodingException e){
	    System.out.println(e);
	}//try
	
	try{
	    url = new URL("https://itunes.apple.com/search?term=" + encoded);//turns the encoded string into a URL
	}catch(java.net.MalformedURLException e){
	    System.out.println(e);
	}//try

	try{
	    reader = new InputStreamReader(url.openStream());//sends to the iTunes API
	}catch(IOException e){
	    System.out.println(e);
	}//try

	JsonParser jp = new JsonParser();
	JsonElement je = jp.parse(reader);

	JsonObject root = je.getAsJsonObject();                      // root of response
	JsonArray results = root.getAsJsonArray("results");          // "results" array

	int numResults = results.size();                             // "results" array size

	if(numResults < 20){
	    this.size = 0;
	}else{
	    this.size = numResults;
	}

	imagesURL = new String[numResults];

	for (int i = 0; i < numResults; i++) {                       
	    JsonObject result = results.get(i).getAsJsonObject();    // object i in array
	    JsonElement artworkUrl100 = result.get("artworkUrl100"); // artworkUrl100 member

	    if (artworkUrl100 != null) {                             // member might not exist
		String artUrl = artworkUrl100.getAsString();        // get member as string
		System.out.println(artUrl);
		imagesURL[i] = artUrl;
	    } // if
	} // for
	displayImages();
    }//query

    /**
     * Displays the images in a gridpane.
     */
    public void displayImages(){

	if(this.size >= 20){ // checks to make sure the size is at least 20
	    images = new ImageView[20]; // creates a new array of image view

        /* places each image in the image view array into the grid pane*/
	for(int i = 0; i < images.length; i++){
	    images[i] = new ImageView(imagesURL[i]);
	}//for

	for(int i = 0; i < 5; i++){
	    grid.setConstraints(images[i], i, 0);
	}//for

	for(int i = 5; i < 10; i++){
	    grid.setConstraints(images[i], i-5, 1);
	}//for

	for(int i = 10; i < 15; i++){
	    grid.setConstraints(images[i], i-10, 2);
	}//for

	for(int i = 15; i < 20; i++){
	    grid.setConstraints(images[i], i-15, 3);
	}//for

	for(int i = 0; i < images.length; i++){
	    grid.getChildren().addAll(images[i]);
	}//for

       }else{
	    /* Creates an exit scene for if images are < 20 */
	   Stage exitStage = new Stage();
	   Text exitMessage = new Text("The query you have searched does not have enough images,\n please close this message and try again, or exit");
	   Button exit = new Button("Exit");
	   HBox exitPane = new HBox();
	  	  
	   exitPane.getChildren().addAll(exitMessage, new Text("    "), exit);
	   exitPane.setPadding(new Insets(10));	  

	   Scene scene = new Scene(exitPane);
	   exitStage.setTitle("Pop-Up");
	   exitStage.setScene(scene);
	   exitStage.sizeToScene();
	   exitStage.show();

	   exit.setOnAction(event -> {
		   Platform.exit();
		   System.exit(0);
	       });
       }//if
   }//displayImages

    /**
     * Changes the album artwork out once when play is hit.
     */
    public void randomImages(){

	ImageView random [];
	Random r = new Random();

	if(this.size > 20){
	    random = new ImageView[this.size];
	    
	    for(int i = 21; i < this.size; i++){
		random[i] = new ImageView(imagesURL[i]); // puts new image url's in the imgs
	    }//for 
	    
	    int numSwapped = r.nextInt(5) + 2;// gets the number of random images to swap
	    
	    for(int i = 1; i <= numSwapped; i++){ // chooses random rows/cols to place the new imaes in
		int randRows = r.nextInt(4); // geneartes random rows
		int randCols = r.nextInt(3); // generates random ols
		int img = r.nextInt(this.size-1) + 21;
		
		grid.setConstraints(random[img], randCols, randRows); // adds to the grid
		grid.getChildren().addAll(random[img]); // adds to the grid
	    }//for	    
	}//if
    }//randomimages 
    
    /**
     * Handles the pgroess bar
     */
    public void progress(){
	
	BorderPane bottom = new BorderPane();
	ProgressBar prog = new ProgressBar(); // creates new progress bar
	
	bottomPane.getChildren().addAll(prog, new Text("\t\t Images provided courtesy of iTunes"));
	bottom.setBottom(bottomPane); // adds to bottom pane
	outerPane.getChildren().addAll(grid, bottom); // adds to pane
	   
    }//progress

    @Override
    public void start(Stage stage){

	Scene scene = new Scene(outerPane);

	stage.setMaxWidth(640);
	stage.setMaxHeight(480);
	stage.setTitle("Search Gallery!");
	stage.setScene(scene);
	stage.sizeToScene();
	stage.show();
	outerPane.getChildren().add(pane);
      
	menu(); //shows menu

	if(firstStart){
	    search(); // shows search
	    query("pop"); // shows query
	    firstStart = false;	
	}
	progress(); // shows progress
    }//start  

    public static void main(String[] args) {
	try {
	    Application.launch(args);
	} catch (UnsupportedOperationException e) {
	    System.out.println(e);
	    System.err.println("If this is a DISPLAY problem, then your X server connection");
	    System.err.println("has likely timed out. This can generally be fixed by logging");
	    System.err.println("out and logging back in.");
	    System.exit(1);
	} // try
    } // main

} // GalleryApp
