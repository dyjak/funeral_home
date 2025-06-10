import { useState, useEffect } from "react";
import FloatingSkeleton from '../components/FloatingSkeleton';
import ImageCarousel from '../components/ImageCarousel'; 
import { AlertProvider } from '../contexts/AlertContext';
import AlertContainer from '../components/common/AlertContainer';

const App = ({ token, setToken }) => {
  return (
    <AlertProvider>
      <AlertContainer />
      <div className="bg-gray-900 text-gray-100 min-h-screen font-serif relative">
        <FloatingSkeleton />
        
        <main className="container mx-auto px-4 py-8">
          {/* Header Section */}
          <div className="text-center mb-12">
            <h1 className="text-4xl mb-4 font-light">
              Aplikacja zakładu pogrzebowego — Wieczny Spokój
            </h1>
            
            {token ? (
              <p className="text-xl text-gray-300">Zalogowano ✅</p>
            ) : (
              <p className="text-xl text-gray-300">
                Nie jesteś zalogowany. Przejdź do logowania.
              </p>
            )}
          </div>

          {/* Two Column Layout */}
          <div className="grid md:grid-cols-2 gap-8 items-start">
            {/* Left Column - Carousel and Links */}
            <div className="space-y-6">
              {/* Carousel */}
              <div className="bg-gray-800 p-6 rounded-lg shadow-lg aspect-video">
                <ImageCarousel />
              </div>

              {/* Links Section */}
              <div className="bg-gray-800 p-6 rounded-lg shadow-lg">
                <h3 className="text-lg font-medium text-gray-300 mb-4">Przydatne linki</h3>
                <div className="space-y-3">
                  <div className="flex items-center gap-2">
                    <span className="text-gray-400">🔗</span>
                    <a 
                      href="https://github.com/PZ-2025/INF_CZARNI" 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="text-purple-400 hover:text-purple-300 transition-colors"
                    >
                      Repozytorium projektu na GitHub
                    </a>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-gray-400">📸</span>
                    <a 
                      href="https://www.pexels.com" 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="text-purple-400 hover:text-purple-300 transition-colors"
                    >
                      Zdjęcia użyte w karuzeli (Pexels)
                    </a>
                  </div>
                </div>
              </div>
            </div>

            {/* Right Column - Project Info */}
            <div className="bg-gray-800 p-6 rounded-lg shadow-lg">
              <h2 className="text-2xl font-semibold mb-6 text-purple-400">
                Informacje o projekcie
              </h2>
              
              <div className="space-y-4">
                <div>
                  <h3 className="text-lg font-medium text-gray-300">Uczelnia</h3>
                  <p className="text-gray-400">Uniwersytet Rzeszowski</p>
                  <p className="text-gray-400">Instytut Informatyki</p>
                  <p className="text-gray-400">Wydział Nauk Ścisłych i Technicznych</p>
                </div>

                <div>
                  <h3 className="text-lg font-medium text-gray-300">Przedmiot</h3>
                  <p className="text-gray-400">Programowanie Zespołowe</p>
                  <p className="text-gray-400">Rzeszów 2025</p>
                </div>

                <div>
                  <h3 className="text-lg font-medium text-gray-300">Zespół CZARNI</h3>
                    <ul className="text-gray-400">
                      <li>Mateusz Hołyszko</li>
                      <li>Faustyna Misiura</li>
                      <li>Michał Dyjak</li>
                      <li>Mateusz Florian</li>
                    </ul>
                </div>


                <div>
                  <h3 className="text-lg font-medium text-gray-300">Technologie</h3>
                  <p className="text-gray-400">Frontend: React, Tailwind CSS</p>
                  <p className="text-gray-400">Backend: Spring Boot, MySQL</p>
                </div>
              </div>
            </div>
          </div>
        </main>
      </div>
    </AlertProvider>
  );
};

export default App;