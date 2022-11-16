################################################################################
# This script joins pdddos and revass based on numDossier column of both files #
# then launch batch process on the output file                                 #
# Author: ZIDANI El Mehdi                                                      #
################################################################################


if [ $# -eq 0 ] || [ $# -lt 6 ]
then
        echo "USAGE: revass_batch_launch.sh <pdddos_file> <cre_file> <revass_file> <output_declaration> <output_revass> <error_file> "
        exit 1
    fi

pdd=$1
cre=$2
rev=$3
outputdec=$4
outputrev=$5
error=$6
outpath="./temp_revass/"$outputdec

################################################################################
# FILE JOIN SECTION                                                            #
################################################################################
[ -d temp_revass ] || mkdir temp_revass

awk 'FNR==1{f++}
f==1{if($4 == "07250")dossier50[$3] = $0;
if($4 == "07101")dossier101[$3] = $0;
if($4 == "07201")dossier201[$3] = $0
if($4 == "07205")
    {
        if(substr($5,83,2)=="02")
            dossier0502[$3] = $0;
        else if (substr($5,83,2)=="03")
            dossier0503[$3] = $0;
    }
if($4 == "07212") dossier12[$3] = $0;

next;
}
f==2{
    if($4 == "00006")
        if(substr($25,0,1)=="+" || substr($25,0,1)=="-")
            cre[$8]=$0;
    next;}
f==3{
    if(dossier50[$9]!="" && dossier101[$9]!=""&& dossier201[$9]!="" && dossier0502[$9]!="" && dossier0503[$9]!="" && cre[$9]!="" && dossier12[$9]!="")
        {
            printf("<client>\n");
            printf"<rev>%s</rev>\n",$0;
            printf"<pdd50>%s</pdd50>\n",dossier50[$9];
            printf"<pdd101>%s</pdd101>\n",dossier101[$9];
            printf"<pdd201>%s</pdd201>\n",dossier201[$9];
            printf"<pdd0502>%s</pdd0502>\n",dossier0502[$9];
            printf"<pdd0503>%s</pdd0503>\n",dossier0503[$9];
            printf"<pdd12>%s</pdd12>\n",dossier12[$9];
            printf"<cre>%s</cre>\n",cre[$9];
            print("</client>")
        }
}' $pdd $cre $rev > $outpath
sed -i '1s/^/<clients>\n/' $outpath
sed -i -e '$a</clients>' $outpath

################################################################################
# BATCH PROCESS SECTION                                                        #
################################################################################
java -jar Revass.jar inpath "./temp_revass/output.txt" revoutpath $outputrev decloutpath $outputdec errpath $error

rm -rf temp_revass