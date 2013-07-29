DIR=`dirname ${0}`
cd ${DIR}
mkdir --parents build
svn \
	list \
	--recursive \
	. \
	> build/makeSourceZipFiles.txt
rm -f build/success/exedio-cops-src-build*.tar.gz
mkdir --parents build/success
tar czf \
	build/success/exedio-cops-src-build${BUILD_NUMBER}.tar.gz \
	--files-from=build/makeSourceZipFiles.txt \
	--no-recursion
