package com.centomila;

import static com.centomila.utils.PopupUtils.*;
import static com.centomila.utils.SettingsHelper.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.NoteStep;
import com.bitwig.extension.controller.api.Setting;
import com.bitwig.extension.controller.api.Signal;

/**
 * Utility class providing methods for manipulating clips in Bitwig Studio.
 * This class contains various static methods for handling clip operations such as
 * loop length modification, step movement, and rotation.
 * 
 * <p>The class primarily works with Bitwig's Clip API and provides functionality for:
 * <ul>
 *   <li>Switching between launcher and arranger clips</li>
 *   <li>Modifying clip loop lengths and playback points</li>
 *   <li>Moving and rotating steps within clips</li>
 *   <li>Initializing clip-related settings and controls</li>
 * </ul>
 * 
 * <p>The class uses a concept of "steps" which represent individual note events
 * within a clip, and provides methods to manipulate these steps either by moving
 * them linearly or rotating them within the clip boundaries.
 * 
 * <p>Note: This class is designed to work with the Bitwig Studio API and requires
 * appropriate initialization of the Bitwig extension system.
 * 
 * @see com.bitwig.extension.controller.api.Clip
 * @see com.bitwig.extension.controller.api.NoteStep
 * @see com.centomila.BitwigBuddyExtension
 * 
 * @author centomila
 * @version 1.0
 */
public class ClipUtils {
    private static String CATEGORY_OTHER = "Other";

    /**
     * Returns the Clip object for either the Arranger Clip Launcher or the Launcher
     * Clip depending on the value of the "Launcher/Arranger" setting.
     *
     * @param toggleSetting The setting used to toggle between launcher and
     *                      arranger.
     * @param arrangerClip  The Arranger Clip.
     * @param cursorClip    The Launcher (cursor) Clip.
     * @return The selected Clip object.
     */
    public static Clip getLauncherOrArrangerAsClip(Setting toggleSetting, Clip arrangerClip, Clip cursorClip) {
        String launcherArrangerSelection = ((EnumValue) toggleSetting).get();
        return launcherArrangerSelection.equals("Arranger") ? arrangerClip : cursorClip;
    }

    /**
     * Sets the loop length of the given clip to a given start and end time in
     * beats.
     * Additionally, sets the playback start and end times to the same values.
     *
     * @param clip      The clip to modify.
     * @param loopStart The loop start time in beats.
     * @param loopEnd   The loop end time in beats.
     */
    public static void setLoopLength(Clip clip, double loopStart, double loopEnd) {
        clip.getLoopStart().set(loopStart);
        clip.getLoopLength().set(loopEnd);
        clip.getPlayStart().set(loopStart);
        clip.getPlayStop().set(loopEnd);
    }

    /**
     * Moves steps in a clip by a given offset. The sort order is determined by the
     * offset
     * direction to prevent overlapping.
     *
     * @param clip        The clip containing note steps.
     * @param stepsToMove The list of steps to move.
     * @param stepOffset  The offset by which to move the steps.
     * @param channel     The channel of the steps.
     */
    public static void moveSteps(Clip clip, List<NoteStep> stepsToMove, int stepOffset, int channel) {
        if (stepOffset > 0) {
            stepsToMove.sort(Comparator.comparingInt(NoteStep::x).reversed());
        } else {
            stepsToMove.sort(Comparator.comparingInt(NoteStep::x));
        }

        for (NoteStep step : stepsToMove) {
            if (step.x() == 0 && stepOffset < 0) {
                // Prevent moving steps before the start of the clip
                stepOffset = 0;
                showPopup("Cannot move steps before the start of the clip");
            } else {
                clip.moveStep(channel, step.x(), step.y(), stepOffset, 0);
            }
        }
    }

    /**
     * Rotates steps in a clip by the given offset with wrapping around at clip
     * boundaries.
     *
     * @param clip          The clip containing note steps.
     * @param stepsToRotate The list of steps to rotate.
     * @param stepOffset    The rotation offset.
     * @param loopLengthInt The loop length in integer steps.
     * @param channel       The channel of the steps.
     */
    public static void rotateSteps(Clip clip, List<NoteStep> stepsToRotate, int stepOffset, int loopLengthInt,
            int channel) {
        // Sort steps in descending order by x coordinate.
        stepsToRotate.sort(Comparator.comparingInt(NoteStep::x).reversed());

        if (stepOffset > 0) { // Rotate forwards
            for (NoteStep step : stepsToRotate) {
                clip.moveStep(channel, step.x(), step.y(), stepOffset, 0);
            }
            for (NoteStep step : stepsToRotate) {
                if (step.x() + stepOffset >= loopLengthInt) {
                    clip.moveStep(channel, step.x() + stepOffset, step.y(), -loopLengthInt, 0);
                }
            }
        } else if (stepOffset < 0) { // Rotate backwards
            int adjustedOffset = (loopLengthInt) - 1;
            for (NoteStep step : stepsToRotate) {
                clip.moveStep(channel, step.x(), step.y(), adjustedOffset, 0);
            }
            for (NoteStep step : stepsToRotate) {
                if (step.x() + adjustedOffset >= loopLengthInt) {
                    clip.moveStep(channel, step.x() + adjustedOffset, step.y(), -loopLengthInt, 0);
                }
            }
        }
    }

    /**
     * Handles moving or rotating steps in a clip based on provided parameters.
     *
     * @param clip            The clip containing note steps.
     * @param channel         The channel of the steps.
     * @param noteDestination The destination note value.
     * @param stepSize        The step size as a string.
     * @param subdivision     The subdivision value.
     * @param stepOffset      The offset to apply.
     * @param isRotate        If true, rotate steps; otherwise, move steps.
     */
    public static void handleStepMovement(Clip clip, int channel, int noteDestination,
            String stepSize, String subdivision, int stepOffset, boolean isRotate) {
        double loopLength = clip.getLoopLength().get();
        double stepsPerBeat = 1.0 / Utils.getNoteLengthAsDouble(stepSize, subdivision);
        int loopLengthInt = (int) Math.round(loopLength * stepsPerBeat);

        List<NoteStep> stepsToMove = new ArrayList<>();
        for (int i = 0; i < 128; i++) {
            NoteStep step = clip.getStep(channel, i, noteDestination);
            if (step != null && step.duration() > 0.0) {
                stepsToMove.add(step);
            }
        }

        if (isRotate) {
            rotateSteps(clip, stepsToMove, stepOffset, loopLengthInt, channel);
        } else {
            moveSteps(clip, stepsToMove, stepOffset, channel);
        }
    }

    /**
     * Initializes the ClipUtils class by creating and configuring settings for
     * clip operations.
     * This method should be called during the extension initialization process.
     *  
     * @param extension The BitwigBuddyExtension object to which the settings will be added.
     */
    public static void init(BitwigBuddyExtension extension) {
        Setting spacerOther = (Setting) createStringSetting(
                "OTHER--------------------------------",
                CATEGORY_OTHER,
                0,
                "---------------------------------------------------");

        disableSetting(spacerOther); // Spacers are always disabled

        Setting clearClipSetting = (Setting) createSignalSetting(
                "Clear current clip",
                CATEGORY_OTHER,
                "Clear current clip");

        Setting clearCurrentNoteDestination = (Setting) createSignalSetting(
                "Clear current note destination",
                CATEGORY_OTHER,
                "Clear current note destination");

        ((Signal) clearClipSetting).addSignalObserver(() -> extension.getLauncherOrArrangerAsClip().clearSteps());
        ((Signal) clearCurrentNoteDestination)
                .addSignalObserver(() -> {
                    int noteDestination = extension.noteDestSettings.getCurrentNoteDestinationAsInt();
                    int noteChannel = extension.noteDestSettings.getCurrentChannelAsInt();
                    extension.getLauncherOrArrangerAsClip().clearStepsAtY(noteChannel, noteDestination);
                });
    }
    
}
