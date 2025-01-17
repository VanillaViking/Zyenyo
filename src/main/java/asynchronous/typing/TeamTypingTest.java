package asynchronous.typing;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import zyenyo.Database;

import commands.Typing;
import dataStructures.TypingSubmission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class TeamTypingTest extends TypingTest
{
	private String[] teamRed;
	private String[] teamBlue;

	public TeamTypingTest(MessageReceivedEvent event, String[] args)
	{
		super(event, args);
		String[] players = Arrays.copyOfRange(args, 1, args.length);

		teamRed = Arrays.copyOfRange(players,0, (int)players.length/2);
		teamBlue = Arrays.copyOfRange(players, (int)players.length/2, players.length);
	}

	@Override
	public void run() throws NumberFormatException
	{
		constructAndSendTest("none");
		event.getJDA().addEventListener(this);
	}
	@Override
	public void quitTest()
	{
		scheduledStop.cancel(true);
		concludeTest.run();
	}


	private Runnable concludeTest = new Runnable()
	{
		@Override
		public void run()
		{
			// Print leaderboard.
			event.getJDA().removeEventListener(thisInstance);
			channel.sendTyping().queue();
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Typing Test Results");

			// Sorting by Typing Points.
			HashMap<Short, TypingSubmission> leaderboardMap = submissions.getMap();
			List<Integer> lbOrder = leaderboardMap.keySet().stream()
					.mapToInt(x -> (int)x)
					.boxed()
					.sorted((a,b)->Double.compare(submissions.getSubmission(b).typingPoints(), submissions.getSubmission(a).typingPoints()))
					.collect(Collectors.toList());

			double teamRedTotal = 0;
			double teamBlueTotal = 0;

                        ArrayList<TypingSubmission> red = new ArrayList<TypingSubmission>();
                        ArrayList<TypingSubmission> blue = new ArrayList<TypingSubmission>();

			for (int i = 0; i < submissions.getNumSubmissions(); i++)
			{


				TypingSubmission s = submissions.getSubmission(lbOrder.get(i));
				System.out.println(s.userID());
				Arrays.asList(teamRed).forEach((String a) -> System.out.println(a));
				Arrays.asList(teamBlue).forEach((String a) -> System.out.println(a));
				if (Arrays.asList(teamRed).contains(String.format("<@%s>", s.userID()))) {red.add(s); teamRedTotal += s.typingPoints();}
				else if (Arrays.asList(teamBlue).contains(String.format("<@%s>", s.userID()))) {blue.add(s); teamBlueTotal += s.typingPoints();}
				else {continue;}
				Database.addTest(s.userID(), s.wordsPerMinute(), s.accuracy(), s.typingPoints());

			}

                        int i = 0;
                        //add submissions strategically so that red and blue teams have thei own columns
                        while (i < red.size() || i < blue.size()) {
                          if (i < red.size()) {
				embed.addField(
						String.format("%s %s ", ":red_square:", red.get(i).userTag()),
						String.format(
								"TP: **`%.2f`**%n",
										red.get(i).typingPoints()),
						true);
                          } else {embed.addField("","",true);}
                          if (i < blue.size()) {
				embed.addField(
						String.format("%s %s ", ":blue_square:", blue.get(i).userTag()),
						String.format(
								"TP: **`%.2f`**%n",
										blue.get(i).typingPoints()),
						true);
                          } else {embed.addField("","",true);}
                          embed.addField("","", true);

                            i++;
                        }

			embed.addField("Team Results", String.format("*Team Red Total:* **`%.2f`**%n"
					+ "*Team Blue Total:* **`%.2f`%n**", 
					teamRedTotal, teamBlueTotal), false);
			double difference = teamRedTotal - teamBlueTotal;
			String winner = difference > 0 ? "Red" : "Blue";

			embed.addField(String.format("**Team %s Wins By: `%.2f` Points!**", winner, Math.abs(difference)), "", false);

			message.replyEmbeds(embed.build()).queue();
			Typing.guildTestList.remove(event.getGuild().getIdLong());
		}
	};
}
