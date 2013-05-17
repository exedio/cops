DIR=`dirname ${0}`
cd ${DIR}
svn \
	list \
	--recursive \
	. \
	> build/makeSourceZipFiles.txt
rm -f build/exedio-cops-src-build*.tar.gz
tar czf \
	build/exedio-cops-src-build${BUILD_NUMBER}.tar.gz \
	--files-from=build/makeSourceZipFiles.txt \
	--no-recursion
