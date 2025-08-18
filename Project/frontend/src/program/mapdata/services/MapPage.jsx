import axios from 'axios';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../style/Map.css';

const { kakao } = window;

const MapPage = () => {
    const navigate = useNavigate();
    const [map, setMap] = useState(null);
    const [markers, setMarkers] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [places, setPlaces] = useState([]);

    useEffect(() => {
        if (window.kakao && window.kakao.maps) {
            const container = document.getElementById('map');
            const options = {
                center: new window.kakao.maps.LatLng(36.3504, 127.3845),
                level: 3,
            };
            const kakaoMap = new window.kakao.maps.Map(container, options);
            setMap(kakaoMap);
            fetchMapData(kakaoMap);
        } else {
            console.warn("Kakao Maps SDK가 로드되지 않았습니다.");
        }
    }, []);

    const fetchMapData = async (mapInstance) => {
        try {
            const response = await axios.get('http://localhost:8080/api/mapdata');
            const mapData = response.data;
            setPlaces(mapData);
            displayMarkers(mapData, mapInstance);
        } catch (error) {
            console.error('맵 데이터를 불러오는 데 실패했습니다.', error);
        }
    };

    const displayMarkers = (placesToDisplay, mapInstance) => {
        if (!mapInstance) return;

        markers.forEach(marker => marker.setMap(null));
        const newMarkers = [];

        placesToDisplay.forEach(place => {
            const position = new kakao.maps.LatLng(place.latitude, place.longitude);
            const marker = new kakao.maps.Marker({
                map: mapInstance,
                position: position,
                title: place.placeName,
            });

            newMarkers.push(marker);
        });

        setMarkers(newMarkers);
    };

    const handleSearchChange = (e) => {
        setSearchQuery(e.target.value);
    };

    const handleSearchSubmit = async (e) => {
        e.preventDefault();
        if (!searchQuery) {
            alert('검색어를 입력해주세요.');
            return;
        }

        try {
            const response = await axios.get(`http://localhost:8080/api/mapdata/search?place=${searchQuery}`);
            const searchResults = response.data;
            setPlaces(searchResults);
            displayMarkers(searchResults, map);

            if (searchResults.length > 0) {
                const firstResult = searchResults[0];
                const moveLatLon = new kakao.maps.LatLng(firstResult.latitude, firstResult.longitude);
                map.setCenter(moveLatLon);
            } else {
                alert('검색 결과가 없습니다.');
            }

        } catch (error) {
            console.error('검색 중 오류 발생:', error);
            alert('검색 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="map-page-container">
            <h1 className="map-title">동물 놀이터 찾기</h1>
            <div className="map-search-area">
                <form className="map-search-form" onSubmit={handleSearchSubmit}>
                    <input
                        type="text"
                        className="map-search-input"
                        placeholder="지역 또는 장소명을 입력하세요"
                        value={searchQuery}
                        onChange={handleSearchChange}
                    />
                    <button type="submit" className="map-search-button">검색</button>
                </form>
                <button className="map-register-button" onClick={() => navigate('/map/map/register')}>장소 등록</button>
            </div>
            
            <div id="map" className="kakao-map-container"></div>
            
            <div className="place-list-container">
                <h3>장소 목록</h3>
                {places.length > 0 ? (
                    <ul className="place-list">
                        {places.map((place) => (
                            <li key={place.mapdataNum} className="place-item">
                                <strong>{place.placeName}</strong> - {place.address}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="no-places-message">등록된 장소가 없습니다.</p>
                )}
            </div>
        </div>
    );
};

export default MapPage;