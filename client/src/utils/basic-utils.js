import tinycolor from "tinycolor2";
import Snackbar from "utils/snackbar";

export const popupNotification = (content, color) => {
    popup(content, color, {showAction: false});
};

export const popupWarning = (content = "Something unexpected happened") => {
    let color;
    if(document.body.classList.contains("dark")) {
        color = tinycolor("hsl(48, 100%, 50%)");
    } else {
        color = tinycolor("hsl(20, 99%, 40%)");
    }
    popup(content, color, {showAction: false, duration: 6000});
};

export const popupError = (content = "Something unexpected happened") => {
    let color;
    if(document.body.classList.contains("dark")) {
        color = tinycolor("hsl(2, 95%, 66%)");
    } else {
        color = tinycolor("hsl(355, 67%, 48%)");
    }
    popup(content, color, {showAction: false, duration: 8000});
};

const popup = (content, theme, data) => {
    Snackbar.show({
        text: content,
        textColor: theme.toString(),
        backgroundColor: "var(--secondary)",
        pos: "bottom-center",
        customStyle: {border: "1px dashed " + theme.setAlpha(.2).toRgbString()},
        ...data
    });
};