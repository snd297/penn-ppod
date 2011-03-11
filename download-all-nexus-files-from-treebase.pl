#!/usr/bin/env perl

for ($i = 1245; $i <= 1999; $i++) {
    system("curl -m 300 -L -f -v -o S$i.nex http://www.treebase.org/treebase-web/phylows/study/TB2:S$i?format=nex");
}
