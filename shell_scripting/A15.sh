#!/bin/bash

print_sys_information(){
    # print all sys information
    echo "Currently logged users"
    whoami
    echo 

    echo "Your shell directory"
    echo $SHELL
    echo 

    echo "Home directory"
    echo $HOME
    echo
    
    echo "OS name & version"
    cat /etc/os-release
    echo

    echo "Current working directory"
    pwd
    echo

    echo "Number of users logged in"
    who | wc -l
    echo

    echo "Show all available shells in your system"
    cat /etc/shells
    echo

    echo "Hard disk information"
    df -h
    echo 

    echo "CPU information"
    lscpu
    echo 

    echo "Memory information"
    free -h
    echo 

    echo "File system information"
    df -T
    echo

    echo "Currently running process"
    ps aux
    echo

    echo "disk usage"
    du
    echo
    
}

main(){
    print_sys_information
}

main 