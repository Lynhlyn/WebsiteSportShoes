<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const BASE_URL = "http://localhost:8080";
const urlSanPham = `${BASE_URL}/san-pham/addSP`;
const urlDanhMuc = `${BASE_URL}/danh-muc`;
const urlThuongHieu = `${BASE_URL}/thuong-hieu`;
const urlChatLieu = `${BASE_URL}/chat-lieu`;
const urlDeGiay = `${BASE_URL}/de-giay`;

const hinhAnh = ref([]);
const newImageUrl = ref("");
const tenSanPham = ref("");
const moTa = ref("");
const danhMuc = ref("");
const thuongHieu = ref("");
const chatLieu = ref("");
const deGiay = ref("");
const trangThai = ref(true);

const danhMucList = ref([]);
const thuongHieuList = ref([]);
const chatLieuList = ref([]);
const deGiayList = ref([]);
const isLoading = ref(false);
const errors = ref({});

// 🖼 **Xử lý chọn ảnh từ máy tính**
const handleChange = (event) => {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = () => {
            hinhAnh.value.push(reader.result);
        };
        reader.readAsDataURL(file);
    }
};

// 🌐 **Hàm kiểm tra URL ảnh hợp lệ**
const isValidURL = (url) => {
    const regex = /^(https?:\/\/.*\.(?:png|jpg|jpeg|gif|svg|webp))$/i;
    return regex.test(url);
};

// 📂 **Thêm ảnh bằng URL**
const addImage = () => {
    if (isValidURL(newImageUrl.value)) {
        hinhAnh.value.push(newImageUrl.value);
        newImageUrl.value = "";
    } else {
        alert("URL ảnh không hợp lệ!");
    }
};

// ❌ **Xóa ảnh khỏi danh sách**
const removeImage = (index) => {
    hinhAnh.value.splice(index, 1);
};

// 🔍 **Tải dữ liệu danh mục, thương hiệu, chất liệu, đế giày**
const fetchDropdownData = async () => {
    try {
        const [resDanhMuc, resThuongHieu, resChatLieu, resDeGiay] = await Promise.all([
            axios.get(urlDanhMuc),
            axios.get(urlThuongHieu),
            axios.get(urlChatLieu),
            axios.get(urlDeGiay),
        ]);
        danhMucList.value = resDanhMuc.data || [];
        thuongHieuList.value = resThuongHieu.data || [];
        chatLieuList.value = resChatLieu.data || [];
        deGiayList.value = resDeGiay.data || [];
    } catch (error) {
        alert("Lỗi tải dữ liệu!");
    }
};

// ✅ **Kiểm tra dữ liệu nhập**
const validateForm = () => {
    errors.value = {};
    if (!hinhAnh.value.length) {
        errors.value.hinhAnh = "Vui lòng thêm ít nhất một ảnh sản phẩm";
    }
    if (!tenSanPham.value) errors.value.tenSanPham = "Tên sản phẩm không được để trống";
    if (!moTa.value || moTa.value.length < 25) errors.value.moTa = "Mô tả phải có ít nhất 25 ký tự";
    if (!danhMuc.value) errors.value.danhMuc = "Vui lòng chọn danh mục";
    if (!thuongHieu.value) errors.value.thuongHieu = "Vui lòng chọn thương hiệu";
    if (!chatLieu.value) errors.value.chatLieu = "Vui lòng chọn chất liệu";
    if (!deGiay.value) errors.value.deGiay = "Vui lòng chọn đế giày";

    return Object.keys(errors.value).length === 0;
};
// 🚀 **Gửi dữ liệu lên API**
const uploadImage = async (file) => {
    try {
        const formData = new FormData();
        formData.append("file", file);

        const response = await axios.post("http://localhost:8080/san-pham/upload", formData, {
            headers: { "Content-Type": "multipart/form-data" }
        });

        return response.data; // Trả về URL ảnh
    } catch (error) {
        console.error("Lỗi upload ảnh:", error);
        return null;
    }
};

const handleAddSanPham = async () => {
    if (!validateForm()) return;

    // 🖼 Upload từng ảnh lên server trước
    const uploadedImages = await Promise.all(
        hinhAnh.value.map(async (base64) => {
            const file = dataURLtoFile(base64, "image.jpg"); // Convert base64 -> File
            return await uploadImage(file);
        })
    );

    // Lọc bỏ ảnh lỗi (null)
    const validImageUrls = uploadedImages.filter(url => url !== null);

    if (validImageUrls.length === 0) {
        alert("Upload ảnh thất bại!");
        return;
    }

    const newSanPham = {
        tenSanPham: tenSanPham.value,
        moTa: moTa.value,
        danhMuc: { id: danhMuc.value },
        thuongHieu: { id: thuongHieu.value },
        chatLieu: { id: chatLieu.value },
        deGiay: { id: deGiay.value },
        trangThai: trangThai.value,
    };

    try {
        await axios.post(urlSanPham, newSanPham, {
            params: { urlAnh: validImageUrls } // ✅ Gửi danh sách URL thay vì base64
        });
        alert("Thêm sản phẩm thành công!");
        router.push('/admin/products/manage');
    } catch (error) {
        console.error("❌ Lỗi API:", error.response?.data || error);
    }
};

// ⚡️ Convert base64 -> File
const dataURLtoFile = (dataurl, filename) => {
    let arr = dataurl.split(",");
    let mime = arr[0].match(/:(.*?);/)[1];
    let bstr = atob(arr[1]);
    let n = bstr.length;
    let u8arr = new Uint8Array(n);

    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, { type: mime });
};


onMounted(fetchDropdownData);
</script>


<template>
    <div class="container">
        <h2 class="text-center mt-4">Thêm sản phẩm mới</h2>

        <div class="card form-container shadow-lg">
                    <form @submit.prevent="handleAddSanPham">
                        <!-- 📂 Upload file ảnh -->
                        <div class="mb-3">
                            <label class="form-label">Thêm ảnh sản phẩm</label>
                            <input type="file" class="form-control" @change="handleChange" accept="image/*">
                            <small v-if="errors.hinhAnh" class="text-danger">{{ errors.hinhAnh }}</small>
                        </div>

                        <!-- 🖼 Danh sách ảnh -->
                        <div class="image-preview-container">
                            <div v-for="(img, index) in hinhAnh" :key="index" class="image-preview">
                                <img :src="img" class="img-thumbnail">
                                <button type="button" class="btn btn-danger btn-sm" @click="removeImage(index)">Xóa</button>
                            </div>
                        </div>
                <div class="mb-3">
                    <label class="form-label">Tên sản phẩm</label>
                    <input v-model="tenSanPham" type="text" class="form-control" placeholder="Nhập tên sản phẩm">
                    <small v-if="errors.tenSanPham" class="text-danger">{{ errors.tenSanPham }}</small>
                </div>

                <div class="mb-3">
                    <label class="form-label">Mô tả</label>
                    <textarea v-model="moTa" class="form-control" placeholder="Nhập mô tả sản phẩm"></textarea>
                    <small v-if="errors.moTa" class="text-danger">{{ errors.moTa }}</small>
                </div>

                <div class="mb-3">
                    <label class="form-label">Danh mục</label>
                    <select v-model="danhMuc" class="form-select">
                        <option value="" disabled>Chọn danh mục</option>
                        <option v-for="dm in danhMucList" :key="dm.id" :value="dm.id">{{ dm.tenDanhMuc }}</option>
                    </select>
                    <small v-if="errors.danhMuc" class="text-danger">{{ errors.danhMuc }}</small>
                </div>

                <div class="mb-3">
                    <label class="form-label">Thương hiệu</label>
                    <select v-model="thuongHieu" class="form-select">
                        <option value="" disabled>Chọn thương hiệu</option>
                        <option v-for="th in thuongHieuList" :key="th.id" :value="th.id">{{ th.tenThuongHieu }}</option>
                    </select>
                    <small v-if="errors.thuongHieu" class="text-danger">{{ errors.thuongHieu }}</small>
                </div>

                <div class="mb-3">
                    <label class="form-label">Chất liệu</label>
                    <select v-model="chatLieu" class="form-select">
                        <option value="" disabled>Chọn chất liệu</option>
                        <option v-for="cl in chatLieuList" :key="cl.id" :value="cl.id">{{ cl.tenChatLieu }}</option>
                    </select>
                    <small v-if="errors.chatLieu" class="text-danger">{{ errors.chatLieu }}</small>
                </div>

                <div class="mb-3">
                    <label class="form-label">Đế giày</label>
                    <select v-model="deGiay" class="form-select">
                        <option value="" disabled>Chọn đế giày</option>
                        <option v-for="dg in deGiayList" :key="dg.id" :value="dg.id">{{ dg.tenDeGiay }}</option>
                    </select>
                    <small v-if="errors.deGiay" class="text-danger">{{ errors.deGiay }}</small>
                </div>

                <div class="mb-3">
                    <label class="form-label">Trạng thái</label>
                    <select v-model="trangThai" class="form-select">
                        <option :value="true">Hoạt động</option>
                        <option :value="false">Ngừng hoạt động</option>
                    </select>
                </div>

                <div v-if="errors.apiError" class="alert alert-danger">{{ errors.apiError }}</div>

                <div class="d-flex gap-3">
                    <button type="submit" class="btn btn-primary btn-lg" :disabled="isLoading">
                        <span v-if="isLoading" class="spinner-border spinner-border-sm"></span>
                        {{ isLoading ? "Đang xử lý..." : "Thêm sản phẩm" }}
                    </button>
                    <button type="button" class="btn btn-secondary btn-lg" @click="router.go(-1)">
                        Quay lại
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<style scoped>
.container {
    max-width: 600px;
    margin: auto;
}

.form-container {
    padding: 2rem;
    border-radius: 10px;
    background: #fff;
}

.form-control,
.form-select {
    border-radius: 5px;
    transition: 0.3s;
}

.form-control:focus,
.form-select:focus {
    border-color: #007bff;
    box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

.btn:hover {
    transform: scale(1.05);
}

.image-preview-container {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
}

.image-preview {
    position: relative;
    display: inline-block;
}

.img-thumbnail {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border: 1px solid #ddd;
}

.btn-danger {
    position: absolute;
    top: 5px;
    right: 5px;
    font-size: 12px;
}
</style>
