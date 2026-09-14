<<<<<<< HEAD
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles.css'
import App from './App'

createRoot(document.getElementById('root')).render(
  <StrictMode><App /></StrictMode>,
)
=======
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.jsx";
import "./index.css";
import { useContext } from "react";
import { AppProvider } from "./Context/Context.jsx";
// import { BrowserRouter as Router } from "react-router-dom";
ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    {/* <Router> */}
      <AppProvider>
        <App />
      </AppProvider>
    {/* </Router> */}
  </React.StrictMode>
);
>>>>>>> e53f6e9c670f659c7447e69cce5809d1c20d9862
