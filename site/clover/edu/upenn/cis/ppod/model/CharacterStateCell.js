var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = {"classes":[{"el":546,"id":175,"methods":[{"el":185,"sc":2,"sl":185},{"el":197,"sc":2,"sl":193},{"el":204,"sc":2,"sl":199},{"el":226,"sc":2,"sl":206},{"el":274,"sc":2,"sl":235},{"el":293,"sc":2,"sl":279},{"el":303,"sc":2,"sl":301},{"el":316,"sc":2,"sl":313},{"el":351,"sc":2,"sl":325},{"el":360,"sc":2,"sl":358},{"el":369,"sc":2,"sl":362},{"el":376,"sc":2,"sl":374},{"el":394,"sc":2,"sl":383},{"el":411,"sc":2,"sl":403},{"el":446,"sc":2,"sl":422},{"el":462,"sc":2,"sl":455},{"el":502,"sc":2,"sl":493},{"el":518,"sc":2,"sl":510},{"el":527,"sc":2,"sl":523},{"el":545,"sc":2,"sl":534}],"name":"CharacterStateCell","sl":64},{"el":116,"id":175,"methods":[{"el":115,"sc":3,"sl":102}],"name":"CharacterStateCell.Type","sl":70}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_0":{"methods":[{"sl":235},{"sl":383},{"sl":403},{"sl":493}],"name":"setTypeAndStatesInapplicableTooManyStates","pass":true,"statements":[{"sl":237},{"sl":238},{"sl":239},{"sl":241},{"sl":242},{"sl":243},{"sl":385},{"sl":393},{"sl":404},{"sl":407},{"sl":408},{"sl":410},{"sl":495},{"sl":496},{"sl":497}]},"test_1":{"methods":[{"sl":235},{"sl":383},{"sl":403},{"sl":493}],"name":"setTypeAndStatesPolymorphicTooFewStates","pass":true,"statements":[{"sl":237},{"sl":238},{"sl":239},{"sl":259},{"sl":260},{"sl":261},{"sl":385},{"sl":393},{"sl":404},{"sl":407},{"sl":408},{"sl":410},{"sl":495},{"sl":496},{"sl":497}]},"test_2":{"methods":[{"sl":235},{"sl":383},{"sl":403},{"sl":493}],"name":"setTypeAndStatesUncertainTooFewStates","pass":true,"statements":[{"sl":237},{"sl":238},{"sl":239},{"sl":265},{"sl":266},{"sl":267},{"sl":385},{"sl":393},{"sl":404},{"sl":407},{"sl":408},{"sl":410},{"sl":495},{"sl":496},{"sl":497}]},"test_3":{"methods":[{"sl":235},{"sl":383},{"sl":403},{"sl":493}],"name":"setTypeAndStatesUnassignedTooManyStates","pass":true,"statements":[{"sl":237},{"sl":238},{"sl":239},{"sl":247},{"sl":248},{"sl":249},{"sl":385},{"sl":393},{"sl":404},{"sl":407},{"sl":408},{"sl":410},{"sl":495},{"sl":496},{"sl":497}]},"test_4":{"methods":[{"sl":206},{"sl":235},{"sl":383},{"sl":422},{"sl":455},{"sl":493}],"name":"setStatesForACellThatDoesNotBelongToARow","pass":true,"statements":[{"sl":207},{"sl":208},{"sl":237},{"sl":238},{"sl":239},{"sl":253},{"sl":254},{"sl":258},{"sl":385},{"sl":393},{"sl":423},{"sl":425},{"sl":426},{"sl":429},{"sl":433},{"sl":434},{"sl":456},{"sl":457},{"sl":458},{"sl":459},{"sl":461},{"sl":495},{"sl":496},{"sl":497},{"sl":499},{"sl":500}]},"test_5":{"methods":[{"sl":235},{"sl":383},{"sl":403},{"sl":493}],"name":"setTypeAndStatesSingleTooManyStates","pass":true,"statements":[{"sl":237},{"sl":238},{"sl":239},{"sl":253},{"sl":254},{"sl":255},{"sl":385},{"sl":393},{"sl":404},{"sl":407},{"sl":408},{"sl":410},{"sl":495},{"sl":496},{"sl":497}]},"test_6":{"methods":[{"sl":185},{"sl":206},{"sl":235},{"sl":279},{"sl":301},{"sl":325},{"sl":358},{"sl":362},{"sl":383},{"sl":403},{"sl":422},{"sl":455},{"sl":493}],"name":"save","pass":true,"statements":[{"sl":207},{"sl":211},{"sl":217},{"sl":237},{"sl":238},{"sl":239},{"sl":247},{"sl":248},{"sl":252},{"sl":253},{"sl":254},{"sl":258},{"sl":259},{"sl":260},{"sl":264},{"sl":280},{"sl":282},{"sl":302},{"sl":326},{"sl":327},{"sl":328},{"sl":329},{"sl":330},{"sl":332},{"sl":335},{"sl":336},{"sl":337},{"sl":338},{"sl":339},{"sl":346},{"sl":359},{"sl":365},{"sl":366},{"sl":368},{"sl":385},{"sl":393},{"sl":404},{"sl":407},{"sl":408},{"sl":410},{"sl":423},{"sl":425},{"sl":426},{"sl":429},{"sl":430},{"sl":433},{"sl":434},{"sl":437},{"sl":439},{"sl":441},{"sl":442},{"sl":444},{"sl":445},{"sl":456},{"sl":457},{"sl":458},{"sl":459},{"sl":461},{"sl":495},{"sl":496},{"sl":497},{"sl":499},{"sl":500},{"sl":501}]},"test_8":{"methods":[{"sl":235},{"sl":383},{"sl":403},{"sl":493}],"name":"setTypeAndStatesSingleTooFewStates","pass":true,"statements":[{"sl":237},{"sl":238},{"sl":239},{"sl":253},{"sl":254},{"sl":255},{"sl":385},{"sl":393},{"sl":404},{"sl":407},{"sl":408},{"sl":410},{"sl":495},{"sl":496},{"sl":497}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6, 4], [6, 4], [4], [], [], [6], [], [], [], [], [], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [8, 2, 6, 5, 3, 0, 1, 4], [], [8, 2, 6, 5, 3, 0, 1, 4], [8, 2, 6, 5, 3, 0, 1, 4], [8, 2, 6, 5, 3, 0, 1, 4], [], [0], [0], [0], [], [], [], [6, 3], [6, 3], [3], [], [], [6], [8, 6, 5, 4], [8, 6, 5, 4], [8, 5], [], [], [6, 4], [6, 1], [6, 1], [1], [], [], [6], [2], [2], [2], [], [], [], [], [], [], [], [], [], [], [], [6], [6], [], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6], [6], [6], [6], [6], [6], [], [6], [], [], [6], [6], [6], [6], [6], [], [], [], [], [], [], [6], [], [], [], [], [], [], [], [], [], [], [], [6], [6], [], [], [6], [], [], [6], [6], [], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [8, 2, 6, 5, 3, 0, 1, 4], [], [8, 2, 6, 5, 3, 0, 1, 4], [], [], [], [], [], [], [], [8, 2, 6, 5, 3, 0, 1, 4], [], [], [], [], [], [], [], [], [], [8, 2, 6, 5, 3, 0, 1], [8, 2, 6, 5, 3, 0, 1], [], [], [8, 2, 6, 5, 3, 0, 1], [8, 2, 6, 5, 3, 0, 1], [], [8, 2, 6, 5, 3, 0, 1], [], [], [], [], [], [], [], [], [], [], [], [6, 4], [6, 4], [], [6, 4], [6, 4], [], [], [6, 4], [6], [], [], [6, 4], [6, 4], [], [], [6], [], [6], [], [6], [6], [], [6], [6], [], [], [], [], [], [], [], [], [], [6, 4], [6, 4], [6, 4], [6, 4], [6, 4], [], [6, 4], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [8, 2, 6, 5, 3, 0, 1, 4], [], [8, 2, 6, 5, 3, 0, 1, 4], [8, 2, 6, 5, 3, 0, 1, 4], [8, 2, 6, 5, 3, 0, 1, 4], [], [6, 4], [6, 4], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], []]
