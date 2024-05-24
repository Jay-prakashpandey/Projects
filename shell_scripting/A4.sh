#!/bin/bash

# Global variable to track SSH login status
is_ssh_logged_in=0
ssh_user=""
ssh_ip=""

# Function to create a connection to SSH
connect_ssh(){
    read -p "Enter username: " ssh_user
    read -p "Enter IP address: " ssh_ip
    ssh "$ssh_user@$ssh_ip"
    if [[ $? -eq 0 ]]; then
        echo "Successfully connected"
        is_ssh_logged_in=1
        return 0
    else
        echo "Unable to connect. Please retry."
        return 1
    fi
}

# Function to perform secure copy
secure_copy_file(){
    local src_file=$1
    local dst_path=$2

    if [[ $is_ssh_logged_in -eq 0 ]]; then
        connect_ssh
        if [[ $? -ne 0 ]]; then
            exit 1
        fi
    fi

    scp $src_file $dst_path
    if [[ $? -eq 0 ]]; then
        echo "Successfully copied to $dst_path"
        return 0
    else
        echo "Unable to copy file $src_file"
        return 1
    fi
}

# Main menu
while true; do
    echo "1. SSH Login"
    echo "2. SCP File Transfer"
    echo "3. Exit"
    read -p "Choose an option: " option

    case $option in
        1)
            connect_ssh
            ;;
        2)
            read -p "Enter source file path: " src_file
            read -p "Enter destination path: " dst_path

            if [[ -z "$src_file" || -z "$dst_path" ]]; then
                echo "Source and destination paths cannot be empty"
                continue
            fi

            echo "1. Local to Remote"
            echo "2. Remote to Local"
            read -p "Choose a direction (1 or 2): " direction

            case $direction in
                1)
                    if [[ ! -f $src_file ]]; then
                        echo "Source file does not exist"
                        continue
                    fi
                    dst_path="$ssh_user@$ssh_ip:$dst_path"
                    ;;
                2)
                    src_file="$ssh_user@$ssh_ip:$src_file"
                    if [[ ! -d $dst_path ]]; then
                        echo "Destination directory does not exist"
                        continue
                    fi
                    ;;
                *)
                    echo "Invalid direction option. Please choose 1 or 2."
                    continue
                    ;;
            esac

            secure_copy_file "$src_file" "$dst_path"
            ;;
        3)
            exit 0
            ;;
        *)
            echo "Invalid option. Please choose 1, 2, or 3."
            ;;
    esac
done
