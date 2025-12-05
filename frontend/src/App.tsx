import {useEffect, useState} from "react";
import type {Property} from "csstype";
import "./App.css";

type APIColoursType = {
     "OK": [Property.Color, Property.Color, Property.Color],
     "Fetching...": [Property.Color, Property.Color, Property.Color],
     "Unreachable": [Property.Color, Property.Color, Property.Color]
};
const APIColours: APIColoursType = {
     "OK": ["green", "#2c3715", "white"],
     "Fetching...": ["yellow", "lightyellow", "black"],
     "Unreachable": ["palevioletred", "red", "white"],
};

function App() {
     const [status, setStatus] = useState<keyof APIColoursType>("Fetching...");

     useEffect(() => {
          fetch("http://localhost:8080/health")
               .then(response => response.text())
               .then(statusText => setStatus(statusText as keyof APIColoursType));
     });

     return (
          <>
               <div>API Status: <span style={{
                    backgroundColor: APIColours[status][1],
                    borderColor: APIColours[status][0],
                    borderStyle: "solid",
                    borderWidth: 2,
                    color: APIColours[status][2],
                    paddingInline: "12.5px",
                    paddingBlock: "5px",
                    borderRadius: "50px",
               }}>{status}</span></div>
          </>
     )
}

export default App;
