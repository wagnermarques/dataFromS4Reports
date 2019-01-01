#/bin/bash
find ./ -name "*.pdf" -print0 | xargs -0 -I f pdftohtml f f.xhmtl


