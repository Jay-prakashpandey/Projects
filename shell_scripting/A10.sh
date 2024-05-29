#!/bon/bash

solve_exp(){
        local exp=$1
        local len=${#exp}
        local res=${exp:0:1}
        local opr=${exp:$len-1:1}
        for((i=1;i<$len-1;i++));
        do
                res=$((res $opr ${exp:$i:1}))
        done

        case $opr in
                "+")
                        opr_name="Sum";;
                "-")
                        opr_name="Sub";;
                "*")
                        opr_name="Mul";;
                *)
                        opr_name="Div";;
        esac

        echo "$opr_name of ${exp:0:$len-1} = $res"
}
main(){
        solve_exp $@
}

main $@
