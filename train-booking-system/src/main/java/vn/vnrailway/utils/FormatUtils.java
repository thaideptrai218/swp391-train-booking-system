package vn.vnrailway.utils;

public class FormatUtils {

    /**
     * Formats a duration given in total minutes into a string representation
     * including days, hours, and minutes (e.g., "1d 2h 30m", "2h 30m", "45m").
     *
     * @param totalMinutes The total duration in minutes.
     * @return A string representing the formatted duration. Returns "N/A" if
     *         totalMinutes is negative, and "0m" if totalMinutes is zero.
     */
    public static String formatDuration(int totalMinutes) {
        if (totalMinutes < 0) {
            return "N/A"; // Or consider throwing an IllegalArgumentException
        }
        if (totalMinutes == 0) {
            return "0m";
        }

        long days = totalMinutes / (24 * 60);
        long remainingMinutesAfterDays = totalMinutes % (24 * 60);
        long hours = remainingMinutesAfterDays / 60;
        long minutes = remainingMinutesAfterDays % 60;

        StringBuilder sb = new StringBuilder();
        boolean hasContent = false;

        if (days > 0) {
            sb.append(days).append("d");
            hasContent = true;
        }
        if (hours > 0) {
            if (hasContent) {
                sb.append(" ");
            }
            sb.append(hours).append("h");
            hasContent = true;
        }
        if (minutes > 0) {
            if (hasContent) {
                sb.append(" ");
            }
            sb.append(minutes).append("m");
            hasContent = true;
        }
        
        // This final check ensures that if all components (days, hours, minutes) are zero 
        // (which should be caught by the initial totalMinutes == 0 check),
        // it still returns "0m". This is more of a safeguard.
        if (!hasContent && totalMinutes > 0) { 
             // This case should ideally not be reached if logic is correct for totalMinutes > 0
             // and all components are 0. It implies an issue if totalMinutes > 0 but d,h,m are all 0.
             // For safety, if totalMinutes was > 0 but somehow no d,h,m was appended, show raw minutes.
             // However, the primary "0m" case is handled at the start.
            sb.append(totalMinutes).append("m (raw)"); // Fallback, should not happen
        } else if (!hasContent && totalMinutes == 0) {
            return "0m"; // Redundant due to initial check, but safe.
        }


        return sb.toString();
    }
}
