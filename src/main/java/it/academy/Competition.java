package it.academy;

import it.academy.classes.Dump;
import it.academy.classes.Scientist;
import it.academy.classes.models.Robot;
import it.academy.classes.threads.Factory;
import it.academy.classes.threads.Minion;
import it.academy.classes.threads.Timer;
import it.academy.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Competition {

	private static List<Scientist> scientists = new ArrayList<>();
	private static List<Thread> threads = new ArrayList<>();

	private static StringBuilder winners = new StringBuilder();
	private static StringBuilder losers = new StringBuilder();

	public static void start() {
		Dump dump = new Dump();
		Factory factory = new Factory(dump, Constants.FACTORY);
		threads.add(factory);

		IntStream.range(0, Constants.COUNT_OF_SCIENTISTS).forEach(i -> {
			Scientist scientist = new Scientist(Constants.SCIENTIST + (i + 1));
			threads.add(new Minion(dump, scientist));
			scientists.add(scientist);
		});

		threads.add(Timer.getInstance());

		threads.forEach(Thread::start);
		threads.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

		findWinner(scientists);
		printResults();

	}

	private static void findWinner(List<Scientist> list) {
		List<Integer> countOfRobots = list.stream()
				.map(scientist -> Robot.createRobots(scientist.getStockMap()))
				.collect(Collectors.toList());

		int maxCount = countOfRobots.stream()
				.max(Integer::compareTo).orElse(0);

		int minCount = countOfRobots.stream()
				.min(Integer::compareTo).orElse(0);

		if (maxCount != 0) {
			if (maxCount == minCount) {
				winners.append("Game draw!\nAll are build ").append(maxCount).append(" robots.");
				losers = null;
			} else {
				for (int i = 0; i < countOfRobots.size(); i++) {
					int count = countOfRobots.get(i);
					if (count == maxCount) {
						winners.append(scientists.get(i).getName()).append(" build ").append(count).append(" robots\n");
					} else {
						losers.append(scientists.get(i).getName()).append(" build ").append(count).append(" robots\n");
					}
				}
			}
		} else {
			winners = null;
			losers.append("All are losers!\nNobody has build any robots.");
		}
	}

	private static void printResults() {

		System.out.println();
		System.out.println("R_E_S_S_U_L_T_S");

		if (winners != null) {
			System.out.println();
			System.out.println("Winners: ");
			System.out.println(winners);
		}
		if (losers != null) {
			System.out.println();
			System.out.println("Losers: ");
			System.out.println(losers);
		}
	}
}
