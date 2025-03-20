<template>
      <div class="container mt-4">
            <!-- Nút mở modal -->
            <button class="btn btn-success" @click="openModal">+ Thêm Khách Hàng</button>

            <!-- Modal -->
            <div v-if="showModal" class="modal" tabindex="-1" style="display:block" aria-labelledby="modalLabel"
                  aria-hidden="false">
                  <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                              <div class="modal-header">
                                    <h5 class="modal-title" id="modalLabel">Thêm Khách Hàng</h5>
                                    <button type="button" class="btn-close" @click="closeModal"
                                          aria-label="Close"></button>
                              </div>

                              <div class="modal-body">
                                    <form @submit.prevent="addCustomer">
                                          <div v-if="errorMessage" class="alert alert-danger text-center">
                                                {{ errorMessage }}
                                          </div>
                                          <div v-if="successMessage" class="alert alert-success text-center">
                                                {{ successMessage }}
                                          </div>

                                          <div class="mb-3">
                                                <label class="form-label">Họ và Tên</label>
                                                <input v-model="newCustomer.hoTen" type="text"
                                                      class="form-control shadow-sm" required />
                                          </div>

                                          <div class="mb-3">
                                                <label class="form-label">Số Điện Thoại</label>
                                                <input v-model="newCustomer.soDienThoai" type="text"
                                                      class="form-control shadow-sm" required />
                                          </div>

                                          <div class="mb-3">
                                                <label class="form-label">Địa Chỉ</label>
                                                <input v-model="newCustomer.diaChi" type="text"
                                                      class="form-control shadow-sm" required />
                                          </div>

                                          <div class="mb-3">
                                                <label class="form-label">Email</label>
                                                <input v-model="newCustomer.email" type="email"
                                                      class="form-control shadow-sm" required />
                                          </div>

                                          <button type="submit" class="btn btn-primary btn-lg shadow-sm"
                                                :disabled="loading">
                                                <span v-if="loading" class="spinner-border spinner-border-sm"></span>
                                                Thêm Khách Hàng
                                          </button>
                                    </form>
                              </div>
                        </div>
                  </div>
            </div>

            <!-- Overlay -->
            <div v-if="showModal" class="modal-backdrop fade show" @click="closeModal"></div>
      </div>
</template>
<script>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();
const newCustomer = ref({
      hoTen: "",
      soDienThoai: "",
      diaChi: "",
      email: "",
});

const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const showModal = ref(false); // Trạng thái modal

// Mở modal
const openModal = () => {
      showModal.value = true;
};

// Đóng modal
const closeModal = () => {
      showModal.value = false;
};

// Hàm thêm khách hàng
const addCustomer = async () => {
      loading.value = true;
      errorMessage.value = "";
      successMessage.value = "";

      try {
            const response = await axios.post("http://localhost:8080/khach-hang", newCustomer.value);
            successMessage.value = "Khách hàng đã được thêm thành công!";
            newCustomer.value = { hoTen: "", soDienThoai: "", diaChi: "", email: "" }; // reset form
            setTimeout(() => {
                  closeModal(); // Đóng modal sau khi thêm khách hàng thành công
                  router.push("/admin/customers/manage"); // điều hướng về danh sách khách hàng
            }, 1500);
      } catch (error) {
            console.error("Lỗi khi thêm khách hàng:", error);
            errorMessage.value = "Không thể thêm khách hàng. Vui lòng thử lại!";
      } finally {
            loading.value = false;
      }
};
</script>
<style scoped>
/* Kiểu modal */
.modal {
      display: block;
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 1050;
      background-color: rgba(0, 0, 0, 0.5);
}

.modal-backdrop {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.5);
}

/* Kiểu dáng dialog modal */
.modal-dialog {
      max-width: 500px;
      /* Điều chỉnh độ rộng của modal */
      margin: auto;
}

/* Nội dung của modal */
.modal-content {
      padding: 20px;
      border-radius: 8px;
      background-color: white;
}

/* Button đóng */
.btn-close {
      background: transparent;
      border: none;
      font-size: 1.5rem;
      color: #000;
}

.modal-body {
      max-height: 70vh;
      overflow-y: auto;
}

/* Đảm bảo kích thước cho các màn hình nhỏ */
@media (max-width: 768px) {
      .modal-dialog {
            margin: 10px;
            max-width: 90%;
      }

      .modal-content {
            padding: 15px;
      }
}
</style>