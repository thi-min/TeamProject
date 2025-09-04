import { useEffect, useState } from 'react';
import '../style/Map.css';

const MapForm = () => {
  const [map, setMap] = useState(null);
  const [places, setPlaces] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeTab, setActiveTab] = useState('hospital');
  const [currentLocation, setCurrentLocation] = useState(null);
  const [markers, setMarkers] = useState([]);
  const kakaoMapKey = '9ef042d2c608fd6bd5f7c5f2658bc1aa';

  // 첫 번째 useEffect: 카카오맵 SDK 로드 및 맵 객체 생성
  useEffect(() => {
    const script = document.createElement('script');
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoMapKey}&libraries=services,clusterer&autoload=false`;
    script.async = true;
    document.head.appendChild(script);

    script.onload = () => {
      window.kakao.maps.load(() => {
        const container = document.getElementById('map');
        const options = {
          center: new window.kakao.maps.LatLng(33.450701, 126.570667),
          level: 3,
        };
        const newMap = new window.kakao.maps.Map(container, options);
        setMap(newMap);
      });
    };
  }, []);

  // 두 번째 useEffect: 맵 객체가 생성된 후 현재 위치 및 초기 검색 수행
  useEffect(() => {
    if (!map) return;

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        const lat = position.coords.latitude;
        const lng = position.coords.longitude;
        const center = new window.kakao.maps.LatLng(lat, lng);
        setCurrentLocation(center);
        map.setCenter(center);

        // 현재 위치 마커 생성
        const currentMarker = new window.kakao.maps.Marker({
          position: center,
          map: map,
        });
        setMarkers([currentMarker]);

        // 초기 장소 검색 실행
        fetchPlacesByRadius(map, center, '동물병원', 3000);
      }, (error) => {
        console.error('위치 정보를 가져오는 데 실패했습니다.', error);
        alert('현재 위치를 가져오지 못했습니다. 장소명을 검색하여 이용해 주세요.');
        // 위치 정보를 가져오지 못했을 경우에도 초기 검색 실행
        fetchPlacesByRadius(map, map.getCenter(), '동물병원', 3000);
      });
    } else {
      console.error('이 브라우저에서는 Geolocation이 지원되지 않습니다.');
      // Geolocation이 지원되지 않는 경우에도 초기 검색 실행
      fetchPlacesByRadius(map, map.getCenter(), '동물병원', 3000);
    }
  }, [map]);

  const fetchPlacesByRadius = (targetMap, center, keyword, radius) => {
    if (!targetMap) return;

    // 기존 마커들 모두 제거
    markers.forEach(marker => marker.setMap(null));
    const newMarkers = [];

    // currentLocation이 유효한 경우에만 마커를 추가
    if (currentLocation) {
      const currentMarker = new window.kakao.maps.Marker({
        position: currentLocation,
        map: targetMap
      });
      newMarkers.push(currentMarker);
    }

    const ps = new window.kakao.maps.services.Places();
    const searchOptions = {
      location: center,
      radius: radius,
      sort: window.kakao.maps.services.SortBy.DISTANCE
    };

    ps.keywordSearch(keyword, (data, status) => {
      if (status === window.kakao.maps.services.Status.OK) {
        const searchResults = data.map(item => ({
          mapdataNum: item.id,
          placeName: item.place_name,
          address: item.address_name,
          latitude: item.y,
          longitude: item.x
        }));
        setPlaces(searchResults);

        const bounds = new window.kakao.maps.LatLngBounds();

        if (currentLocation) {
          bounds.extend(currentLocation);
        }

        searchResults.forEach(place => {
          const markerPosition = new window.kakao.maps.LatLng(place.latitude, place.longitude);
          const marker = new window.kakao.maps.Marker({
            position: markerPosition
          });
          marker.setMap(targetMap);
          newMarkers.push(marker);
          bounds.extend(markerPosition);
        });

        setMarkers(newMarkers);

        if (searchResults.length > 0) {
          targetMap.setBounds(bounds);
        }

      } else {
        console.log("반경 내 검색 결과가 없습니다.");
        setPlaces([]);
      }
    }, searchOptions);
  };

  const handleTabClick = (tabName) => {
    setActiveTab(tabName);
    setPlaces([]);

    if (!map || !currentLocation) return;

    if (tabName === 'hospital') {
      fetchPlacesByRadius(map, currentLocation, '동물병원', 3000);
    } else if (tabName === 'playground') {
      fetchPlacesByRadius(map, currentLocation, '애견놀이터', 3000);
    }
  };

  const handleSearch = () => {
    if (!searchQuery.trim()) {
      alert('검색어를 입력해주세요.');
      return;
    }

    if (!map) {
      alert('지도가 로드되지 않았습니다.');
      return;
    }

    markers.forEach(marker => marker.setMap(null));
    const newMarkers = [];

    // 검색 시 현재 위치 마커를 유지하고 싶다면 이 부분을 추가
    if (currentLocation) {
      const currentMarker = new window.kakao.maps.Marker({
        position: currentLocation,
        map: map,
      });
      newMarkers.push(currentMarker);
    }

    const ps = new window.kakao.maps.services.Places();

    ps.keywordSearch(searchQuery, (data, status) => {
      if (status === window.kakao.maps.services.Status.OK) {
        const searchResults = data.map(item => ({
          mapdataNum: item.id,
          placeName: item.place_name,
          address: item.address_name,
          latitude: item.y,
          longitude: item.x
        }));
        setPlaces(searchResults);

        const bounds = new window.kakao.maps.LatLngBounds();

        // 검색 결과 마커 추가
        searchResults.forEach(place => {
          const markerPosition = new window.kakao.maps.LatLng(place.latitude, place.longitude);
          const marker = new window.kakao.maps.Marker({
            position: markerPosition
          });
          marker.setMap(map);
          newMarkers.push(marker);
          bounds.extend(markerPosition);
        });

        setMarkers(newMarkers);

        if (searchResults.length > 0) {
          map.setBounds(bounds);
        }

      } else if (status === window.kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 없습니다.');
        setPlaces([]);
      } else {
        alert('검색 중 오류가 발생했습니다.');
      }
    });
  };

  const handlePlaceClick = (lat, lng) => {
    if (map) {
      const moveLatLon = new window.kakao.maps.LatLng(lat, lng);
      map.setCenter(moveLatLon);
      map.setLevel(2);
    }
  };

  const handleGetDirections = (destination) => {
    if (!currentLocation) {
      alert('현재 위치 정보를 가져올 수 없습니다. 잠시 후 다시 시도해 주세요.');
      return;
    }

    const originUrl = `https://map.kakao.com/?sName=내 위치&eName=${destination}`;
    window.open(originUrl, '_blank');
  };

  return (
    <div className="map-container">
      <div className="map-wrapper">
        <div id="map"></div>
      </div>
      <div className="list-wrapper">
        <div className="list-header">
          <div className="temp_form md w60p">
            <input
              className="temp_input"
              type="text"
              placeholder="장소명 검색"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <button className="form-button-primary " onClick={handleSearch}>검색</button>
        </div>
        <div className="list-header">
          <div className="tab-form_btn_box">
            <button className={`tab-item ${activeTab === 'hospital' ? 'active' : ''}`} onClick={() => handleTabClick('hospital')}>동물병원</button>
          </div>
          <div className="tab-form_btn_box">
            <button className={`tab-item ${activeTab === 'playground' ? 'active' : ''}`} onClick={() => handleTabClick('playground')}>애견놀이터</button>
          </div>

          <div className="search-box">
          </div>
        </div>
        <ul className="place-list">
          {places.length > 0 ? (
            places.map((place) => (
              <li key={place.mapdataNum} onClick={() => handlePlaceClick(place.latitude, place.longitude)}>
                <h4>{place.placeName}</h4>
                <p>{place.address}</p>
                <button
                  className="form-button-primary "
                  onClick={(e) => {
                    e.stopPropagation();
                    handleGetDirections(place.placeName);
                  }}
                >
                  길찾기
                </button>
              </li>
            ))
          ) : (
            <li>검색 결과가 없습니다.</li>
          )}
        </ul>
      </div>
    </div>
  );
};

export default MapForm;