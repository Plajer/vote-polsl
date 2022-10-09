import styled from "@emotion/styled";
import axios from "axios";
import UsosLoginModal from "components/UsosLoginModal";
import React, {useEffect, useState} from "react";
import tinycolor from "tinycolor2";
import {UiCard, UiLoadingSpinner, UiThemeContext} from "ui";
import {UiButton, UiLoadableButton} from "ui/button";
import {UiCol, UiContainer, UiRow} from "ui/grid";
import {popupError, popupNotification, popupWarning} from "utils/basic-utils";
import {getEnvVar} from "utils/env-vars";
import Cookies from "js-cookie";

axios.interceptors.response.use(undefined, error => {
    if (error.response === undefined) {
        popupError("Serwer chyba sie zepsul.");
        return Promise.reject(error);
    }
    if (error.response.status === 500) {
        popupError("Blad po stronie serwera.");
    }
    if (error.response.data.errors !== undefined) {
        error.response.data.errors.forEach(err => popupWarning(err));
    }
    return Promise.reject(error);
});

export const API_ROUTE = getEnvVar("REACT_APP_SERVER_IP_ADDRESS") + "/api/v1";
// Minimum Web Content Accessibility Guidelines contrast ratio for text
export const WCAG_AA_CONTRAST = 3.0;

export const ChosenNumber = styled.div`
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: red;
  color: var(--font-color);
  font-weight: bold;
  font-size: 1.4rem;
  width: auto;
  padding-left: .5rem;
  padding-right: .5rem;
  height: 2.25rem;
  text-align: center;
  border-top-left-radius: .5rem;
  border-bottom-right-radius: var(--border-radius);

  & > div {
    transform: translate(2px, 2px);
  }
`;

const App = () => {
    const [options, setOptions] = useState({data: [], loaded: false, error: false});
    const [chosen, setChosen] = useState([]);
    const [usosData, setUsosData] = useState({data: {}, requestLink: null, loggedIn: false});
    useEffect(() => {
        axios.defaults.baseURL = API_ROUTE;
        axios.get("/options").then(res => {
            setOptions({...options, data: res.data, loaded: true});
        });

        if (!usosData.loggedIn) {
            const cookie = Cookies.get("usos_data");
            if(cookie) {
                const data = JSON.parse(cookie);
                if(data.student_number) {
                    setUsosData({...usosData, data: JSON.parse(cookie), loggedIn: true});
                    return;
                }
            }
            axios.get("/oauth/request").then(res => {
                setUsosData({...usosData, requestLink: res.data});
            });
        }
    }, []);

    const onSend = () => {
        if (!usosData.loggedIn) {
            popupWarning("Musisz wpierw się zalogować!");
            return Promise.resolve();
        }
        return axios.post("/request", {
            indexId: usosData.data.student_number,
            requestedOptions: chosen
        }).then(() => popupNotification("Pomyślnie wysłano", tinycolor("#00FF00")));
    };
    const onChoose = option => {
        if (chosen.some(o => o.id === option.id)) {
            let newChosen = [...chosen.filter(o => o.id !== option.id)];
            let i = 1;
            newChosen = newChosen.map(o => {
                const data = {id: o.id, priority: i};
                i++;
                return data;
            });
            setChosen(newChosen);
            return;
        }
        const newChosen = [...chosen];
        const toInsert = {id: option.id, priority: chosen.length === 0 ? 1 : chosen[chosen.length - 1].priority + 1};
        newChosen.push(toInsert);
        setChosen(newChosen);
    };
    const onCodeEnter = code => {
        return axios.post("/oauth/access", {code, authUrl: usosData.requestLink}).then(res => {
            setUsosData({...usosData, data: res.data, loggedIn: true});
            Cookies.set("usos_data", JSON.stringify(res.data), {expires: 30, sameSite: "strict"});
        });
    };
    const renderOptions = () => {
        if (options.error) {
            return <div style={{color: "red"}}>cos sie wyjebalo</div>
        }
        if (!options.loaded) {
            return <UiLoadingSpinner className={"my-4"}/>
        }
        return <React.Fragment>
            <div className={"mb-3"}>
                <div>Poniżej wybierz projekty które chciałbyś otrzymać.</div>
                <div>Kolejność wybierania oznacza większe zainteresowanie danym projektem.</div>
                <div>[A] to zadania z Algorytmionu, [OI] z Olimpiady informatycznej (edycja 1 = rok 1994, numer zadania tak jak pokolei występują etapami itd)</div>
                <div className={"text-red"}><strong>Uwaga</strong>, możesz wybrać tylko raz, w razie jakichkolwiek problemów napisz do Erwina.</div>
            </div>
            <UiRow style={{maxHeight: "500px", overflowY: "auto"}}>
                {options.data.map(o => {
                    const isChosen = chosen.find(op => op.id === o.id);
                    return <UiCol className={"my-1"} key={o.id} xs={6} md={4} lg={3}>
                        <UiCard bodyClassName={"p-2"} style={{cursor: "pointer"}} onClick={() => onChoose(o)}>
                            {isChosen && <ChosenNumber>{isChosen.priority}</ChosenNumber>}
                            {o.name}
                        </UiCard>
                    </UiCol>
                })}
            </UiRow>
        </React.Fragment>
    };
    return <UiThemeContext.Provider value={{
        darkMode: true,
        getTheme: () => tinycolor("#ffa500"),
        theme: () => tinycolor("#ffa500"),
        defaultTheme: () => tinycolor("#ffa500"),
    }}>
        <UsosLoginModal isOpen={!usosData.loggedIn} onHide={void 0} actionButtonName={"Zatwierdź"} actionDescription={<React.Fragment>
            <div>Wejdź w poniższy link z USOS w nowej karcie i poniżej podaj kod wygenerowany przez stronę.</div>
            <div>Ta aplikacja pobiera jedynie te potrzebne dane: Imie, nazwisko, index</div>
            <div><strong>Link do USOS Login: </strong> {usosData.requestLink ? <a href={usosData.requestLink} target={"_blank"} rel={"noreferrer"}>{usosData.requestLink}</a> : <span>ładuję...</span>}</div>
        </React.Fragment>} onAction={onCodeEnter}/>
        <UiContainer>
            <UiRow centered className={"text-center mt-5"}>
                <UiCol xs={12}>
                    <div style={{fontSize: "1.5rem"}}>Matematyka Stosowana - Informatyka</div>
                    <div style={{fontSize: "1.5rem"}}>Wybór projektu</div>
                    <div className={"text-black-60"}>
                        Wybierz kilka projektów które możesz napisać, jeśli twój pierwszy projekt z listy zostanie zabrany zostanie wybrany następny aż do skutku.
                        <br/>
                        <strong>Koniec zapisów we wtorek o 20:00.</strong>
                    </div>
                </UiCol>
                <UiCol xs={12} lg={9} className={"my-3 text-center"}>
                    {usosData.loggedIn && <div style={{fontWeight: "bold"}}>Witaj {usosData.data.first_name} ({usosData.data.student_number})</div>}
                    {renderOptions()}
                </UiCol>
                <UiCol xs={12}>
                    <UiLoadableButton label={"Wyslij"} onClick={onSend}>Wyślij zgłoszenie</UiLoadableButton>
                </UiCol>
            </UiRow>
        </UiContainer>
    </UiThemeContext.Provider>
};

export default App;