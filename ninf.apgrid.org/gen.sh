
FILES="NinfDemo.shtml related.shtml welcome.shtml staff.shtml topics.shtml"
for f in $FILES ; do
		echo $f
		python ~/projects/python/ssi.py  < $f > ${f%.shtml}.ssi.html
done
cp welcome.ssi.html index.html

cd bricks
FILES="collaborators.shtml publications_j.shtml contents.shtml index.shtml related_work.shtml publications.shtml staff.shtml"
for f in $FILES ; do
		echo $f
		python ~/projects/python/ssi.py  < $f > ${f%.shtml}.ssi.html
done

cd ../papers
FILES="papers-2002.j.shtml	papers.j.shtml papers-old.j.shtml papers.shtml"
for f in $FILES ; do
		echo $f
		python ~/projects/python/ssi.py  < $f > ${f%.shtml}.ssi.html
done

cd ../packages
FILES="download-fft-scalapack.shtml	download-g4.shtml download-g1.shtml download-g5.shtml	sponsor.shtml download-g2.shtml	download-v1.shtml welcome.shtml download-g3.shtml	license.shtml"
for f in $FILES ; do
		echo $f
		python ~/projects/python/ssi.py  < $f > ${f%.shtml}.ssi.html
done

cd ../tutorial
FILES="welcome.shtml"
for f in $FILES ; do
		echo $f
		python ~/projects/python/ssi.py  < $f > ${f%.shtml}.ssi.html
done


