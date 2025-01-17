package zyenyo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BotConfig
{
	public static final char PREFIX = '\\';
	public static HashMap<Integer, Double> promptRatingMap = new HashMap<>();
	public static ArrayList<List<Integer>> promptDifficultyList = new ArrayList<List<Integer>>(4);
	
	public static final String BOT_DATA_FILEPATH = "ZBotData/";
	public static final String SCRAPE_DATA_FILEPATH = "ZBotData/ScrapeData/";
	public static final String INDEX_COUNTS_FILEPATH = "ZBotData/ScrapeData/COUNTS.zbif"; //ZBIF = ZyenyoBotIndexFile.
	public static final String INDEX_IDS_FILEPATH = "ZBotData/ScrapeData/IDs.zbif";
	public static final int NUM_PROMPTS = 34;
	public static final List<Long> ADMINISTRATOR_IDS = List.of(642193466876493829l, 365691073156087819l);
	
	private static final File PROMPT_RATING_FILE = new File("ZBotData/TypingPrompts/PromptRatingMap.zbo");
	private static final File PROMPT_DIFFICULTY_FILE = new File("ZBotData/TypingPrompts/SortedPromptsList.zbo");
	
	@SuppressWarnings("unchecked")
	protected static void setConfigVars()
	{
		if (!PROMPT_RATING_FILE.exists() || !PROMPT_DIFFICULTY_FILE.exists()) {CalculatePromptDifficulty.recalculatePromptRatings();}
		
		ObjectInputStream ratingMapOIS=null, difficultyListOIS=null;
		try
		{
			ratingMapOIS = new ObjectInputStream(new FileInputStream(PROMPT_RATING_FILE));
			difficultyListOIS = new ObjectInputStream(new FileInputStream(PROMPT_DIFFICULTY_FILE));
			
			promptRatingMap = (HashMap<Integer, Double>)ratingMapOIS.readObject();
			System.out.println("[LOADED] Prompt Rating Map File");
			
			promptDifficultyList = (ArrayList<List<Integer>>)difficultyListOIS.readObject();
			System.out.println("[LOADED] Prompt Difficulty Categorisation File");
		}
		catch(IOException | ClassNotFoundException e) {e.printStackTrace();}
		finally
		{
			try
			{
				if (ratingMapOIS != null) {ratingMapOIS.close();}
				if (difficultyListOIS != null) {difficultyListOIS.close();}
			}
			catch (IOException e) {e.printStackTrace();}
		}
	}
}
