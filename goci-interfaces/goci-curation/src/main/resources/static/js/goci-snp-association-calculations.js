// Calculates various values used in association form

// Calculate OR per copy num
$(document).ready(function() {
    $("#calculate-or").click(function() {
        var orPerCopyRecip = document.forms["variant-association-form"]["orPerCopyRecip"].value;
        calculateOrPerCopyNum(orPerCopyRecip);
    });
});

function calculateOrPerCopyNum(orPerCopyRecip) {
    var orPerCopyNum;
    orPerCopyNum = Math.round(100 / orPerCopyRecip) / 100;
    console.log(orPerCopyNum);
    document.getElementById("orPerCopyNum").value = orPerCopyNum;
}

// Build orPerCopyRange
$(document).ready(function() {
    $("#calculate-or-range").click(function() {
        var orPerCopyStdError = document.forms["variant-association-form"]["orPerCopyStdError"].value;
        var orPerCopyNum = document.forms["variant-association-form"]["orPerCopyNum"].value;
        setRange(orPerCopyStdError, orPerCopyNum);
    });
});


/* This method calculates the confidence interval based on the standard error
 taken from Kent's Coldfusion code.*/

function setRange(orPerCopyStdError, orPerCopyNum) {
    var delta = Math.round(100000 * orPerCopyStdError * 1.96) / 100000;
    var ORpercopylow = orPerCopyNum - delta;
    var ORpercopyhigh = (1 * orPerCopyNum) + delta;

    if (ORpercopylow < .001) {
        var ORpercopylow2 = ORpercopylow.toFixed(5);
        var ORpercopyhigh2 = ORpercopyhigh.toFixed(5);
    }
    else if (ORpercopylow < .01) {
        ORpercopylow2 = ORpercopylow.toFixed(4);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(4);
    }
    else if (ORpercopylow < .1) {
        ORpercopylow2 = ORpercopylow.toFixed(3);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(3);
    }
    else {
        ORpercopylow2 = ORpercopylow.toFixed(2);
        ORpercopyhigh2 = ORpercopyhigh.toFixed(2);
    }

    document.getElementById("orPerCopyRange").value = '[' + ORpercopylow2 + '-' + ORpercopyhigh2 + ']';
}

// Calculate reciprocal orPerCopyRange

$(document).ready(function() {
    $("#calculate-recip-range").click(function() {
        var orPerCopyRecipRange = document.forms["variant-association-form"]["orPerCopyRecipRange"].value;

        calculateOrPerCopyRange(orPerCopyRecipRange);
    });
});


function calculateOrPerCopyRange(orPerCopyRecipRange) {
    orPerCopyRecipRange = orPerCopyRecipRange.replace("[", "");
    orPerCopyRecipRange = orPerCopyRecipRange.replace("]", "");

    var ci = orPerCopyRecipRange.split("-");

    var one = parseFloat(ci[0].trim());
    var two = parseFloat(ci[1].trim());

    var high = ((100 / one) / 100);
    var low = ((100 / two) / 100);

    var lowval;
    var highval;

    if (low < 0.001) {
        lowval = parseFloat(low).toFixed(5);
        highval = parseFloat(high).toFixed(5);
    }
    else if (low >= 0.001 && low < 0.01) {
        lowval = parseFloat(low).toFixed(4);
        highval = parseFloat(high).toFixed(4);
    }
    else if (low >= 0.01 && low < 0.1) {
        lowval = parseFloat(low).toFixed(3);

        highval = parseFloat(high).toFixed(3);
    }
    else {
        lowval = parseFloat(low).toFixed(2);
        highval = parseFloat(high).toFixed(2);
    }

    document.getElementById("orPerCopyRange").value = '[' + lowval + '-' + highval + ']';

}