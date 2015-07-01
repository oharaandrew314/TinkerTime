package io.andrewohara.tinkertime.controllers;

import io.andrewohara.tinkertime.models.mod.Mod;

public abstract class ModExceptions {
	
	@SuppressWarnings("serial")
	public static class ModNotDownloadedException extends Exception {
		public ModNotDownloadedException(Mod mod, String message){
			super("Error for " + mod + ": " + message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class CannotDisableModError extends Error {
		
	}
	
	@SuppressWarnings("serial")
	public static class ModUpdateFailedError extends Error {
		public ModUpdateFailedError(Exception e){
			super(e);
		}
		public ModUpdateFailedError(Mod mod, String message){
			super("Error for " + mod + ": " + message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class NoModSelectedException extends Exception {
		
	}
	
	@SuppressWarnings("serial")
	public static class CannotDeleteModException extends Exception {
		public CannotDeleteModException(Mod mod, String reason){
			super(String.format("Cannot delete %s: %s", mod, reason));
		}
	}
}
