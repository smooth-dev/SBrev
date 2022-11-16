FNR==1{f++}
f==1{if($4 == "07250")dossier50[$3] = $0;
if($4 == "07101")dossier01[$3] = $0;
if($4 == "07205")
    {
        if(substr($5,83,2)=="02")
            dossier0502[$3] = $0;
        else if (substr($5,83,2)=="03")
            dossier0503[$3] = $0;
    }
next;
}
f==2{
    if($4 == "00006")
        if(substr($25,0,1)=="+" || substr($25,0,1)=="-")
            cre[$8]=$0;
    next;}
f==3{
    if(dossier50[$9]!="" && dossier01[$9]!="" && dossier0502[$9]!="" && dossier0503[$9]!="" && cre[$9]!="")
        {
            printf("<client>\n");
            printf"<rev>%s</rev>\n<pdd50>%s</pdd50>\n<pdd01>%s</pdd01>\n<pdd0502>%s</pdd0502>\n<pdd0503>%s</pdd0503>\n<cre>%s</cre>\n",$0,dossier50[$9],dossier01[$9],dossier0502[$9],dossier0503[$9],cre[$9];
            print("</client>")
        }
}