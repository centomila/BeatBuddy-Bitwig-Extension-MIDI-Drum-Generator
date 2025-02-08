package com.centomila;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.centomila.ReadFile;

// import com.bitwig.extension.controller.api.Action;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.NoteStep;
import com.bitwig.extension.controller.api.Preferences;
// import com.bitwig.extension.controller.api.Cursor;
// import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.EnumDefinition;
import com.bitwig.extension.controller.api.RangedValue;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.SettableRangedValue;
// import com.bitwig.extension.controller.api.SettableIntegerValue;
// import com.bitwig.extension.controller.api.SettableBooleanValue;
// import com.bitwig.extension.controller.api.SettableStringArrayValue;
// import com.bitwig.extension.controller.api.BooleanValue;
// import com.bitwig.extension.controller.api.SettableDoubleValue;
import com.bitwig.extension.controller.api.SettableBeatTimeValue;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.Signal;

public class BeatBuddyExtension extends ControllerExtension {
   private Application application;
   private Clip cursorClip;
   private Clip arrangerClip;
   // private Track track;

   private DocumentState documentState;
   private Setting patternSelectorSetting;
   private Setting noteLengthSetting; // How long each note should be
   private Setting stepSizSetting;
   private Setting stepSizSubdivisionSetting;
   private Setting noteDestinationSetting;
   private Setting noteOctaveSetting;
   private Setting noteChannelSetting;
   private Setting toggleLauncherArrangerSetting;
   private Setting autoResizeLoopLengthSetting;
   private Setting autoReversePatternSetting;
   private Setting moveRotateStepsSetting;
   private NoteDestinationSettings noteDestSettings;

   private Setting spacer1;
   private Setting spacer2;
   private Setting spacer3;
   private Setting spacer4;

   protected BeatBuddyExtension(final BeatBuddyExtensionDefinition definition, final ControllerHost host) {
      super(definition, host);
   }

   @Override
   public void init() {
      final ControllerHost host = getHost();
      PopupUtils.initialize(host);
      // Initialize API objects
      application = host.createApplication();
      cursorClip = host.createLauncherCursorClip((16 * 8), 128);
      arrangerClip = host.createArrangerCursorClip((16 * 8), 128);
      documentState = host.getDocumentState();
      
      // Initialize test file reader
      // new TestFileReader(host, documentState);

      cursorClip.getLoopLength().markInterested();
      cursorClip.getLoopStart().markInterested();
      cursorClip.getPlayStart().markInterested();
      cursorClip.getPlayStop().markInterested();
      arrangerClip.getLoopLength().markInterested();
      arrangerClip.getLoopStart().markInterested();
      arrangerClip.getPlayStart().markInterested();
      arrangerClip.getPlayStop().markInterested();

      initMoveStepsSetting();

      initPatternSetting();

      initNoteDestinationSetting();

      initStepSizeSetting();

      initPostActionSetting();

      initClearClipSetting();

      // Initialize launcher/arranger toggle
      initToggleLauncherArrangerSetting();

      // Show a notification to confirm initialization
      PopupUtils.showPopup("BeatBuddy Initialized");

   }

   /**
    * Sets up the Step Size setting and the Note Length setting.
    * When the user changes the Step Size, the Note Length is automatically updated
    * to match the selected step size.
    * The user can also change the Note Length independently of the Step Size.
    */
   private void initStepSizeSetting() {
      stepSizSetting = (Setting) documentState.getEnumSetting("Step Size", "Clip", Utils.STEPSIZE_OPTIONS, "1/16");

      stepSizSubdivisionSetting = (Setting) documentState.getEnumSetting("Subdivisions", "Clip",
            Utils.STEPSIZE_CATEGORY_OPTIONS, "Straight");

      // set the note length equal to the selected step size
      ((EnumValue) stepSizSetting).addValueObserver(newValue -> {

         // Set both note length and step size
         ((SettableEnumValue) stepSizSetting).set(newValue);
         ((SettableEnumValue) noteLengthSetting).set(newValue);
      });

      // Steps note length
      noteLengthSetting = (Setting) documentState.getEnumSetting("Note Length", "Clip", Utils.STEPSIZE_OPTIONS, "1/16");

      ((EnumValue) noteLengthSetting).addValueObserver(newValue -> {
         ((SettableEnumValue) noteLengthSetting).set(newValue);
      });
   }

   /**
    * Initializes the note destination setting, which includes the note destination
    * dropdown, note octave dropdown, and note channel number setting.
    * 
    * The note destination dropdown has options for all 12 notes, and the value of
    * the dropdown is stored in the currentNoteAsString variable.
    * 
    * The note octave dropdown has options for all octaves from -2 to 8, and the
    * value of the dropdown is stored in the currentOctaveAsInt variable.
    * 
    * The note channel setting is a number setting that allows the user to select a
    * channel from 1 to 16.
    */
   private void initNoteDestinationSetting() {
      // Note destination dropdown
      String[] NOTEDESTINATION_OPTIONS = Utils.NOTE_NAMES;
      noteDestinationSetting = (Setting) documentState.getEnumSetting("Note Destination", "Note Destination",
            NOTEDESTINATION_OPTIONS,
            NOTEDESTINATION_OPTIONS[0]);

      // Note OCT destination dropdown
      String[] OCTAVEDESTINATION_OPTIONS = Arrays.stream(Utils.NOTE_OCTAVES)
            .mapToObj(String::valueOf)
            .toArray(String[]::new);
      noteOctaveSetting = (Setting) documentState.getEnumSetting("Note Octave", "Note Destination",
            OCTAVEDESTINATION_OPTIONS,
            OCTAVEDESTINATION_OPTIONS[3]);

      noteChannelSetting = (Setting) documentState.getNumberSetting("Note Channel", "Note Destination", 1, 16, 1,
            "Channel MIDI", 1);

      // Initialize NoteDestinationSettings
      noteDestSettings = new NoteDestinationSettings(getHost(), noteChannelSetting, NOTEDESTINATION_OPTIONS[0], 3);

      ((EnumValue) noteDestinationSetting).addValueObserver(newValue -> {
         noteDestSettings.setCurrentNote(newValue);
      });

      ((EnumValue) noteOctaveSetting).addValueObserver(newValue -> {
         noteDestSettings.setCurrentOctave(Integer.parseInt(newValue));
      });

      // Empty string for spacing
      spacer2 = (Setting) documentState.getStringSetting("----", "Clip", 0,
            "---------------------------------------------------");
      spacer2.disable();
      // Pattern step size
   }

   /**
    * Initializes the pattern setting, which includes the generate button, pattern
    * dropdown, and reverse pattern checkbox.
    * 
    * The generate button is a signal that triggers the generateDrumPattern method
    * when clicked.
    * 
    * The pattern dropdown has options for all patterns that are defined in the
    * DrumPatterns class.
    * 
    * The reverse pattern checkbox is a pseudo-boolean setting that is used to
    * reverse the pattern before applying it to the clip.
    * 
    * The spacer1 setting is an empty string used for spacing.
    */
   private void initPatternSetting() {
      // Generate button
      Signal generateButton = documentState.getSignalSetting("Generate!", "Generate", "Generate!");

      generateButton.addSignalObserver(() -> {
         generateDrumPattern();
      });

      // Define pattern settings
      final String[] PATTERN_OPTIONS = Arrays.stream(DrumPatterns.patterns)
            .map(pattern -> pattern[0].toString())
            .toArray(String[]::new);
      patternSelectorSetting = (Setting) documentState.getEnumSetting("Pattern", "Generate", PATTERN_OPTIONS,
            "Random");
      ((EnumValue) patternSelectorSetting).addValueObserver(newValue -> {
         PopupUtils.showPopup(newValue.toString());
      });

      autoReversePatternSetting = (Setting) documentState.getEnumSetting("Reverse Pattern", "Generate",
            new String[] { "Normal", "Reverse" }, "Normal");

      // Empty string for spacing
      spacer1 = (Setting) documentState.getStringSetting("----", "Generate", 0,
            "---------------------------------------------------");
      spacer1.disable();
   }

   /**
    * Initializes the move steps setting, which includes the move steps button, a
    * toggleable button that moves the pattern by one step when clicked.
    * 
    * The button has three states: "<<<", "|", and ">>>". The "|"
    * state is the default, and the other two states are used to move the pattern
    * backwards or forwards by one step respectively.
    * 
    * When the button is clicked, the moveSteps method is called with the
    * corresponding argument, and the button is reset to the "|" state.
    */
   private void initMoveStepsSetting() {
      moveRotateStepsSetting = (Setting) documentState.getEnumSetting("Move/Rotate", "Move Steps",
            new String[] { "Move", "Rotate" }, "Move");

      Signal moveFwd = documentState.getSignalSetting("Move Steps Forward", "Move Steps",
            ">>>");
      moveFwd.addSignalObserver(() -> {
         moveSteps(1);

      });

      Signal moveBwd = documentState.getSignalSetting("Move Steps Backward", "Move Steps",
            "<<<");
      moveBwd.addSignalObserver(() -> {
         moveSteps(-1);

      });

      // Empty string for spacing
      Setting spacerMoverSetting = (Setting) documentState.getStringSetting("----", "Move Steps", 0,
            "---------------------------------------------------");
      spacerMoverSetting.disable();
   }

   /**
    * Initializes the post action setting, which includes an empty string spacer
    * and the auto resize loop length setting.
    * 
    * The auto resize loop length setting is a toggleable setting that allows the
    * user to automatically resize the clip length after generating a pattern.
    * 
    * The spacer is an empty string setting that is used for spacing.
    */
   private void initPostActionSetting() {
      // Empty string for spacing
      spacer3 = (Setting) documentState.getStringSetting("----", "Post Actions", 0,
            "---------------------------------------------------");
      spacer3.disable();

      autoResizeLoopLengthSetting = (Setting) documentState.getEnumSetting("Auto resize loop length", "Post Actions",
            new String[] { "Off", "On" }, "On");
   }

   /**
    * Initializes the clear clip setting, which includes an empty string spacer and
    * the clear
    * current clip setting.
    * 
    * The clear current clip setting is a button that, when clicked, will clear the
    * current
    * clip of all notes.
    * 
    * The spacer is an empty string setting that is used for spacing.
    */
   private void initClearClipSetting() {
      // Empty string for spacing
      spacer4 = (Setting) documentState.getStringSetting("----", "Clear Clip", 0,
            "---------------------------------------------------");
      spacer4.disable();

      // Clear current clip
      documentState.getSignalSetting("Clear current clip", "Clear Clip", "Clear current clip").addSignalObserver(() -> {
         getLauncherOrArrangerAsClip().clearSteps();
      });
   }

   /**
    * Initializes the Launcher/Arranger toggle setting, which determines whether
    * the
    * drum pattern is generated in the Launcher or Arranger clip.
    * 
    * The setting is an EnumValue that can be set to either "Launcher" or
    * "Arranger".
    */
   private void initToggleLauncherArrangerSetting() {
      // Launcher/Arranger toggle
      final String[] TOGGLE_LAUNCHER_ARRANGER_OPTIONS = new String[] { "Launcher", "Arranger", };
      toggleLauncherArrangerSetting = (Setting) documentState.getEnumSetting("Destination Launcher/Arranger", "Z",
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS,
            TOGGLE_LAUNCHER_ARRANGER_OPTIONS[0]);
   }

   /**
    * Generates a drum pattern in the currently selected clip.
    * 
    * The pattern is determined by the currently selected pattern, note length, and
    * step size.
    * 
    * If the currently selected pattern is "Random", a random pattern is generated.
    * 
    * If the auto reverse pattern setting is "On", the pattern is reversed.
    * 
    * If the auto resize loop length setting is "On", the loop length is resized to
    * fit
    * the generated pattern.
    */
   private void generateDrumPattern() {
      Clip clip = getLauncherOrArrangerAsClip();
      // if the clip doesn't exist, create it
      // track.createNewLauncherClip(0, 1);

      String noteLength = ((EnumValue) noteLengthSetting).get(); // Get the current selected value of noteLength
      String subdivision = ((EnumValue) stepSizSubdivisionSetting).get();
      String stepSize = ((EnumValue) stepSizSetting).get(); // Get the current selected value of stepSize
      double duration = Utils.getNoteLengthAsDouble(noteLength, subdivision);

      double patternStepSize = Utils.getNoteLengthAsDouble(stepSize, subdivision);
      clip.setStepSize(patternStepSize);

      int channel = noteDestSettings.getCurrentChannelAsInt();
      int noteDestination = noteDestSettings.getCurrentNoteDestinationAsInt();

      clip.clearStepsAtY(channel, noteDestination);

      String selectedPattern = ((EnumValue) patternSelectorSetting).get();
      int[] pattern = DrumPatterns.getPatternByName(selectedPattern);

      if (((EnumValue) autoReversePatternSetting).get().equals("Reverse")) {
         // reverse the pattern array using java arrays
         for (int i = 0; i < pattern.length / 2; i++) {
            int temp = pattern[i];
            pattern[i] = pattern[pattern.length - 1 - i];
            pattern[pattern.length - 1 - i] = temp;
         }

      }

      // if selected pattern = "random", generate a random pattern
      if (selectedPattern.equals("Random")) {
         Random random = new Random();
         for (int i = 0; i < 16; i++) {
            pattern[i] = random.nextInt(128);
            // randomly change the value to 0
            if (random.nextInt(4) == 0) {
               pattern[i] = 0;
            }
         }
      }

      for (int i = 0; i < pattern.length; i++) {
         if (pattern[i] > 0) {
            clip.setStep(channel, i, noteDestination, pattern[i], duration);
         }
      }

      // Post generation actions
      if (((EnumValue) autoResizeLoopLengthSetting).get().equals("On")) {
         double beatLength = patternStepSize * pattern.length;
         ClipUtils.setLoopLength(clip, 0.0, beatLength);
      }

      clip.selectStepContents(channel, noteDestination, false);

      // application.zoomToFit();

   }

   /**
    * Returns the Clip object for either the Arranger Clip Launcher or the Launcher
    * Clip depending on the value of the "Launcher/Arranger" setting.
    * 
    * @return A Clip object, either the Arranger Clip Launcher or the Launcher
    *         Clip.
    */
   private Clip getLauncherOrArrangerAsClip() {
      return ClipUtils.getLauncherOrArrangerAsClip(toggleLauncherArrangerSetting, arrangerClip, cursorClip);
   }

   public void moveSteps(int stepOffset) {
      Clip clip = getLauncherOrArrangerAsClip();
      int channel = noteDestSettings.getCurrentChannelAsInt();
      int noteDestination = noteDestSettings.getCurrentNoteDestinationAsInt();
      String stepSize = ((EnumValue) stepSizSetting).get();
      String subdivision = ((EnumValue) stepSizSubdivisionSetting).get();
      boolean isRotate = ((EnumValue) moveRotateStepsSetting).get().equals("Rotate");

      ClipUtils.handleStepMovement(clip, channel, noteDestination, stepSize, subdivision, stepOffset, isRotate);
   }

   @Override
   public void exit() {
      // Cleanup on exit
      PopupUtils.showPopup("BeatBuddy Exited");
   }

   @Override
   public void flush() {
      // Update logic if necessary
   }
}
