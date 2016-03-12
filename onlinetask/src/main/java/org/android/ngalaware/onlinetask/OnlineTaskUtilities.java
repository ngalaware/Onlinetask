package org.android.ngalaware.onlinetask;

/**
 * Created by user on 11/03/2016.
 */
public class OnlineTaskUtilities
{
    public static OnlineTaskUtilities instance;

    public final String DEBUG_TAGS = "ONLINE_TASK_DEBUG";
    public final String ERROR_TAGS = "ONLINE_TASK_ERROR";

    private boolean enableDebugMode = false;

    /**
     * Handling utilities to support Online task
     * @return instance of Online task constant class
     */
    public static OnlineTaskUtilities getInstance ()
    {
        if (instance == null)
        {
            instance = new OnlineTaskUtilities();
        }
        return instance;
    }

    /**
     * Enable or disable debug mode
     * @param isTrue TRUE or FALSE
     */
    public void SetDebugMode (boolean isTrue)
    {
        this.enableDebugMode = isTrue;
    }

    /**
     * Method to obtain debugging mode is true or false
     * @return enable or disable debug mode
     */
    public boolean GetDebugMode ()
    {
        return this.enableDebugMode;
    }
}
