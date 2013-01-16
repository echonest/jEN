/*
 * (c) 2009  The Echo Nest
 * See "license.txt" for terms
 *
 */
package com.echonest.api.v4.util;


/**
 * An interface implemented by command functions typically
 * added to a command interpreter
 *
 * @see Shell
 */

public interface ShellCommand {

    /**
     * Execute the given command.
     *  
     * @param ci	the command interpretere that invoked this command.
     * @param args	command line arguments (just like main).
     * @return		a command result
     *
     */
    public String execute(Shell ci, String[] args) throws Exception;

    /**
     * Returns a one line description of the command
     *
     * @return a one-liner help message
     */
    public String getHelp();
}
