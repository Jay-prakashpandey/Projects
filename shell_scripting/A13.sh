#!/bin/bash

print_chess_board_nxn(){
        n=$1
        for ((i=0;i<$n;i++));do
                for((j=0;j<$n;j++));do
                        if (( (j+i)%2==0 )); then
                        # '\e[40m' sets the background color to black, and '\e[47m' sets the background color to white.
                        # '\e[0m' resets the color to default.
                                echo -e -n "\e[40m  \e[0m"
                        else
                                echo -e -n "\e[47m  \e[0m"
                        fi
                done
                echo ""
        done
}

main(){
        print_chess_board_nxn $1
}

main $@
