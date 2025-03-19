export async function getProvinces() {
    try {
        const response = await fetch('https://provinces.open-api.vn/api/?depth=1');
        return await response.json();
    } catch (error) {
        console.error("Lỗi khi lấy danh sách tỉnh/thành phố:", error);
        return [];
    }
}

export async function getDistricts(provinceCode) {
    if (!provinceCode) return [];
    try {
        const response = await fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`);
        const data = await response.json();
        return data.districts || [];
    } catch (error) {
        console.error("Lỗi khi lấy danh sách quận/huyện:", error);
        return [];
    }
}

export async function getWards(districtCode) {
    if (!districtCode) return [];
    try {
        const response = await fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`);
        const data = await response.json();
        return data.wards || [];
    } catch (error) {
        console.error("Lỗi khi lấy danh sách phường/xã:", error);
        return [];
    }
}
