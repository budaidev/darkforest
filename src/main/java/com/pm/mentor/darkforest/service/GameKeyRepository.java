package com.pm.mentor.darkforest.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

@Component
public class GameKeyRepository {

	private final String gameKeyHistoryFilePath = "gameKeyHistory.txt";
	private final String gameIdHistoryFilePath = "gameIdHistory.txt";
	
	@SneakyThrows
	public void newGameKeyCreated(String gameKey, long createdAt) {
		Files.write(Paths.get(gameKeyHistoryFilePath), String.format("%d %s\n", createdAt, gameKey).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
	}
	
	@SneakyThrows
	public void newGameCreated(String gameId, long createdAt) {
		Files.write(Paths.get(gameIdHistoryFilePath), String.format("%d %s\n", createdAt, gameId).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
	}
	
	@SneakyThrows
	public List<TimeStampedString> getAllGameKeys() {
		try {
			return Files.readAllLines(Paths.get(gameKeyHistoryFilePath))
				.stream()
				.map(line -> {
					val items = line.split(" ");
					return new TimeStampedString(Long.parseLong(items[0]), items[1]);
				})
				.collect(Collectors.toList());
		} catch (java.nio.file.NoSuchFileException e) {
			return List.of();
		}
	}
	
	@SneakyThrows
	public List<TimeStampedString> getAllGameIds() {
		try {
			return Files.readAllLines(Paths.get(gameIdHistoryFilePath))
				.stream()
				.map(line -> {
					val items = line.split(" ");
					return new TimeStampedString(Long.parseLong(items[0]), items[1]);
				})
				.collect(Collectors.toList());
		} catch (java.nio.file.NoSuchFileException e) {
			return List.of();
		}
	}
	
	@Value
	public class TimeStampedString {
		private final long createdAt;
		private final String value;
	}
}
