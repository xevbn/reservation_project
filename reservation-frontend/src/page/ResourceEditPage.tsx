import { ResourceEditForm } from '../features/resources/components/ResourceEditForm';
import { useResource } from '../features/resources/hook/useResource';

const css = `/* 전체 컨테이너 */
.resourceEditForm {
  max-width: 600px;
  margin: 40px auto;
  padding: 24px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  font-family: 'Pretendard', sans-serif;
}

/* 리소스 리스트 */
.resource-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 개별 리소스 항목 */
.resource-entity {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background-color: #f8f9fa;
  border: 1px solid #eee;
  border-radius: 12px;
  transition: all 0.2s;
}

.resource-entity:hover {
  border-color: #e0e0e0;
  background-color: #f1f3f5;
}

.resource-entity h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

/* 버튼 그룹 */
.btn-grp {
  display: flex;
  gap: 8px;
}

/* 공통 버튼 스타일 */
.btn-grp button, .post-modal-btn, .logout {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: opacity 0.2s;
}

.btn-grp button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 수정 버튼 */
.edit-btn {
  background-color: #e7f5ff;
  color: #228be6;
}

.edit-btn:hover:not(:disabled) {
  background-color: #d0ebff;
}

/* 삭제 버튼 */
.delete-btn {
  background-color: #fff5f5;
  color: #fa5252;
}

.delete-btn:hover:not(:disabled) {
  background-color: #ffe3e3;
}

/* 추가 버튼 (리스트 하단) */
.post-modal-btn {
  width: 100%;
  margin-top: 12px;
  background-color: #228be6;
  color: white;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
}

.post-modal-btn:hover {
  background-color: #1c7ed6;
}

/* 로그아웃 버튼 */
.logout {
  display: block;
  margin: 32px auto 0;
  background: none;
  color: #868e96;
  text-decoration: underline;
  font-size: 13px;
}

.logout:hover {
  color: #343a40;
}`;

export const ResourceEditPage = () => {
  const { data: resources } = useResource();

  return <ResourceEditForm resources={resources} css={css} />;
};
