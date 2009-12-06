var clover = new Object();

// JSON: {classes : [{name, id, sl, el,  methods : [{sl, el}, ...]}, ...]}
clover.pageData = {"classes":[{"el":234,"id":891,"methods":[{"el":88,"sc":2,"sl":88},{"el":97,"sc":2,"sl":90},{"el":111,"sc":2,"sl":105},{"el":122,"sc":2,"sl":113},{"el":130,"sc":2,"sl":124},{"el":141,"sc":2,"sl":132},{"el":153,"sc":2,"sl":143},{"el":151,"sc":6,"sl":147},{"el":161,"sc":2,"sl":155},{"el":171,"sc":2,"sl":163},{"el":175,"sc":2,"sl":173},{"el":179,"sc":2,"sl":177},{"el":198,"sc":2,"sl":190},{"el":206,"sc":2,"sl":203},{"el":211,"sc":2,"sl":208},{"el":232,"sc":2,"sl":219}],"name":"PPodEntity","sl":60}]}

// JSON: {test_ID : {"methods": [ID1, ID2, ID3...], "name" : "testXXX() void"}, ...};
clover.testTargets = {"test_0":{"methods":[{"sl":173}],"name":"setTypeAndStatesPolymorphicTooFewStates","pass":true,"statements":[{"sl":174}]},"test_1":{"methods":[{"sl":173}],"name":"setTypeAndStatesUncertainTooFewStates","pass":true,"statements":[{"sl":174}]},"test_2":{"methods":[{"sl":173}],"name":"setStatesForACellThatDoesNotBelongToARow","pass":true,"statements":[{"sl":174}]},"test_3":{"methods":[{"sl":173}],"name":"setTypeAndStatesUnassignedTooManyStates","pass":true,"statements":[{"sl":174}]},"test_4":{"methods":[{"sl":173}],"name":"setTypeAndStatesSingleTooManyStates","pass":true,"statements":[{"sl":174}]},"test_5":{"methods":[{"sl":173}],"name":"setTypeAndStatesSingleTooFewStates","pass":true,"statements":[{"sl":174}]},"test_6":{"methods":[{"sl":88},{"sl":90},{"sl":124},{"sl":155},{"sl":173},{"sl":190}],"name":"save","pass":true,"statements":[{"sl":91},{"sl":92},{"sl":94},{"sl":95},{"sl":96},{"sl":125},{"sl":128},{"sl":156},{"sl":157},{"sl":174},{"sl":191},{"sl":194},{"sl":195},{"sl":197}]},"test_7":{"methods":[{"sl":173}],"name":"setTypeAndStatesInapplicableTooManyStates","pass":true,"statements":[{"sl":174}]},"test_8":{"methods":[{"sl":173}],"name":"setWrongOTUs","pass":true,"statements":[{"sl":174}]}}

// JSON: { lines : [{tests : [testid1, testid2, testid3, ...]}, ...]};
clover.srcFileLines = [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6], [], [6], [6], [6], [], [6], [6], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6], [6], [], [], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6], [6], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [7, 1, 8, 5, 2, 6, 4, 3, 0], [7, 1, 8, 5, 2, 6, 4, 3, 0], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [6], [6], [], [], [6], [6], [], [6], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], [], []]
