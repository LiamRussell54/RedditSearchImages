import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchImages implements ActionListener{
	
	//user agent to comply with reddit API rules
	public static final String USER_AGENT = "java:com.RedditSearchImages:v1.2.3 (by /u/LRussell54)";
	
	//declaring GUI elements	
	private JFrame frame;
	private JPanel panel;
	private JLabel title;
	private static JButton btnSearch;
	private JTextField txtSearch;
	private JLabel topPosts;
	private static JTextField urlOne;
	private static JTextField urlTwo;
	private static JTextField urlThree;
	
	

	public static void main(String[] args) {
		//calls constructor and loads GUI
		new SearchImages();		
	}//main
	
	public SearchImages() {
		//design of the GUI
		frame = new JFrame();
		
		title = new JLabel("Search reddit /r/pics!", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(24.0f));
		//button that has an action listener, calls the search method when clicked.
		btnSearch = new JButton("Search!");
		btnSearch.addActionListener(this);
		//search box
		txtSearch = new JTextField();
		topPosts = new JLabel("Top 3 most popular images, from www.reddit.com");
		/*text fields to display the url responses
		text fields have been used so the responses can be highlighted and copied.
		no background or border so the text field looks like a label*/
		urlOne = new JTextField("1:");
		urlOne.setEditable(false);
		urlOne.setBackground(null);
		urlOne.setBorder(null);
		urlTwo = new JTextField("2:");
		urlTwo.setEditable(false);
		urlTwo.setBackground(null);
		urlTwo.setBorder(null);
		urlThree = new JTextField("3:");
		urlThree.setEditable(false);
		urlThree.setBackground(null);
		urlThree.setBorder(null);
		
		//creates border and layout for the panel. Adds all the GUI elements
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10,30));
		panel.setLayout(new GridLayout(0,1));
		panel.add(title);
		panel.add(txtSearch);
		panel.add(btnSearch);
		panel.add(topPosts);
		panel.add(urlOne);
		panel.add(urlTwo);
		panel.add(urlThree);
		
		//frame set up
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Search Reddit Images");
		frame.setSize(600,300);
		frame.setResizable(false);
		frame.setVisible(true);
	}//GUI constructor
	
	public static void search(String searchParam) {
		try {
			//passes the parameters passed by the user to the url and opens a connection
			String url = "https://www.reddit.com/r/pics/search.json?q="+searchParam+"&restrict_sr=on&sort=comments";
			URLConnection connection = (new URL(url)).openConnection();
			
			//confirms the url being requested
			System.out.println("Sending request to " + url);
			Thread.sleep(2000); //Delay to comply with rate limiting
			connection.setRequestProperty("User-Agent", USER_AGENT);

			//reader to read in each line of the JSON response
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			//creates JSON objects and an array to get to the level the children are on
			JSONObject myResponse = new JSONObject(response.toString());	
			JSONObject data = new JSONObject(myResponse.getJSONObject("data").toString());
			JSONArray array = new JSONArray(data.getJSONArray("children").toString());
			
			//Checks the size of the array to see how many responses there are.
			if(array.length()==0) {
				//If the array is empty, inform the user. Reset the text fields
				urlOne.setText("1: No results found.");
				urlTwo.setText("2:");
				urlThree.setText("3:");
			}else if(array.length()==1) {
				//for one response get and display the url and tell the user there are no more responses
				JSONObject child = new JSONObject(array.getJSONObject(0).toString());
				JSONObject childData = new JSONObject(child.getJSONObject("data").toString());
				urlOne.setText("1: " + childData.getString("permalink"));
				urlTwo.setText("2: Only one result.");
				urlThree.setText("3:");
			}else if(array.length()==2) {
				//for two responses get and display the urls and tell the user there are no more responses
				JSONObject child = new JSONObject(array.getJSONObject(0).toString());
				JSONObject childTwo = new JSONObject(array.getJSONObject(1).toString());

				JSONObject childData = new JSONObject(child.getJSONObject("data").toString());
				JSONObject childDataTwo = new JSONObject(childTwo.getJSONObject("data").toString());

				urlOne.setText("1: " + childData.getString("permalink"));
				urlTwo.setText("2: " + childDataTwo.getString("permalink"));
				urlThree.setText("3: Only two results.");
			}else{
				//for any more get the top three responses and display the urls
				JSONObject child = new JSONObject(array.getJSONObject(0).toString());
				JSONObject childTwo = new JSONObject(array.getJSONObject(1).toString());
				JSONObject childThree = new JSONObject(array.getJSONObject(2).toString());
				
				JSONObject childData = new JSONObject(child.getJSONObject("data").toString());
				JSONObject childDataTwo = new JSONObject(childTwo.getJSONObject("data").toString());
				JSONObject childDataThree = new JSONObject(childThree.getJSONObject("data").toString());
				
				urlOne.setText("1: " + childData.getString("permalink"));
				urlTwo.setText("2: " + childDataTwo.getString("permalink"));
				urlThree.setText("3: " + childDataThree.getString("permalink"));
			}	
			
		} catch (Exception e) {
			//catch and print exception
			System.out.println(e);
		}
	}//search

	@Override
	public void actionPerformed(ActionEvent e) {
		//checks the users input is valid before calling the search function
		if(txtSearch.getText() == null || txtSearch.getText().trim().isEmpty()) {
			urlOne.setText("1: Invalid Search!");
			urlTwo.setText("2:");
			urlThree.setText("3:");
		}else {
			//trims empty space from the input and replaces the spaces to make a valid url
			String search = txtSearch.getText();
			String searchParam = search.trim().replaceAll(" ", "%20");
			search(searchParam);
		}
	}//action performed

}//class
