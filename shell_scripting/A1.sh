#!/bin/bash

#check if folder is passed through argument
if [[ $# -lt 1 ]] ; then
        # take input folder in which list the files
        read -p "give dir to list all files info: " argv
else
        # argv is the argument folder
        argv=$1
fi

#check if arg is directory
if [[ -d $argv ]]
then
        #echo $(realpath ${argv})
        cd $(realpath ${argv})
        if [[ $? -ne 0 ]]; then
                echo "failed to go to folder"
                exit 1
        else
                res=$(ls "-ltr")
                ls -ltr
                #echo ${res}
        fi
else
        echo "no_folder ${argv}"
fi
exit 0