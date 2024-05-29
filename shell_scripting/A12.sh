#!/bin/bash

length_of_each_words(){
    sentance=$@
    for((i=0;i<${#sentance};i++));
    do
      w=$(echo $sentance | awk -F' ' "{print \$${i}}")
      if [[ -n "$w" ]]; then
          echo "length of ${w} is ${#w}"
      fi
  done
    }

length_of_each_words $@

